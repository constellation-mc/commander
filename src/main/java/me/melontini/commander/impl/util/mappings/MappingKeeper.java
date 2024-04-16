package me.melontini.commander.impl.util.mappings;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.melontini.commander.impl.Commander;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.adapter.MappingSourceNsSwitch;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MappingTreeView;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import net.fabricmc.tinyremapper.IMappingProvider;
import org.objectweb.asm.Type;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

@Log4j2
public final class MappingKeeper {

    public static final String NAMESPACE = FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace();

    @Getter(lazy = true)
    private static final MemoryMappingTree offMojmap = loadOffMojmap();
    @Getter(lazy = true)
    private static final MemoryMappingTree offTarget = loadOffTarget();
    @Getter(lazy = true)
    private static final MemoryMappingTree mojmapTarget = loadMojmapTarget();

    @SneakyThrows
    public static MemoryMappingTree loadMojmapTarget() {
        if (NAMESPACE.equals("mojang")) return getMojmapTarget();

        var tree = new MemoryMappingTree();
        MemoryMappingTree temp = new MemoryMappingTree();
        getOffMojmap().accept(temp);
        getOffTarget().accept(temp);
        log.info("Merging mappings...");
        temp.accept(new MappingSourceNsSwitch(tree, "mojang", true));
        return tree;
    }

    @SneakyThrows
    public static MemoryMappingTree loadOffMojmap() {
        var tree = new MemoryMappingTree();
        log.info("Loading official->mojmap mappings...");
        MappingReader.read(Commander.COMMANDER_PATH.resolve("mappings/client_mappings.txt"), new MappingSourceNsSwitch(tree, "target"));
        tree.setSrcNamespace("official");
        tree.setDstNamespaces(List.of("mojang"));
        return tree;
    }

    @SneakyThrows
    public static MemoryMappingTree loadOffTarget() {
        var tree = new MemoryMappingTree();
        log.info("Loading official->{} mappings...", NAMESPACE);
        MappingReader.read(new InputStreamReader(Objects.requireNonNull(MappingKeeper.class.getClassLoader().getResourceAsStream("mappings/mappings.tiny"), "mappings/mappings.tiny is not available?")), tree);
        return tree;
    }

    public static String toNamed(Field field) {
        return Commander.getRemapper().getEnvironment().getRemapper().mapFieldName(Type.getInternalName(field.getDeclaringClass()), field.getName(), Type.getDescriptor(field.getType()));
    }

    public static String toNamed(Method method) {
        return Commander.getRemapper().getEnvironment().getRemapper().mapMethodName(Type.getInternalName(method.getDeclaringClass()), method.getName(), Type.getMethodDescriptor(method));
    }

    private static IMappingProvider.Member memberOf(String className, String memberName, String descriptor) {
        return new IMappingProvider.Member(className, memberName, descriptor);
    }

    public static IMappingProvider create(MappingTree first, String from, String to) {
        return (acceptor) -> {
            int fromId = first.getNamespaceId(from);
            int toId = first.getNamespaceId(to);

            for (MappingTree.ClassMapping classDef : first.getClasses()) {
                String className = classDef.getName(fromId);
                if (className == null) continue;

                String dstName = getName(classDef, toId, fromId);

                acceptor.acceptClass(className, dstName);
                for (MappingTree.FieldMapping field : classDef.getFields()) {
                    var fieldId = memberOf(className, field.getName(fromId), field.getDesc(fromId));
                    if (fieldId.name == null) continue;

                    try {
                        acceptor.acceptField(fieldId, getName(field, toId, fromId));
                    } catch (Exception e) {
                        throw new RuntimeException("from: %s to:%s cls:%s dstCls:%s field:%s fieldMth:%s"
                                .formatted(from, to, className, dstName, fieldId.name, getName(field, toId, fromId)));
                    }
                }

                for (MappingTree.MethodMapping method : classDef.getMethods()) {
                    var methodIdentifier = memberOf(className, method.getName(fromId), method.getDesc(fromId));
                    if (methodIdentifier.name == null) continue;

                    try {
                        acceptor.acceptMethod(methodIdentifier, getName(method, toId, fromId));
                    } catch (Exception e) {
                        throw new RuntimeException("from: %s to:%s cls:%s dstCls:%s mth:%s dstMth:%s"
                                .formatted(from, to, className, dstName, methodIdentifier.name, getName(method, toId, fromId)));
                    }
                }
            }
        };
    }

    private static String getName(MappingTree.ClassMapping classDef, int id, int fallback) {
        var s = classDef.getName(id);
        if (s != null) return s;
        return classDef.getName(fallback);
    }

    public static String getName(MappingTreeView.MemberMappingView method, int id, int fallback) {
        var s = method.getName(id);
        if (s != null) return s;
        return method.getName(fallback);
    }
}
