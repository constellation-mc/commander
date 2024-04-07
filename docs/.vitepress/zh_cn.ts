import { defineConfig } from 'vitepress'

export const zh_cn = defineConfig({
    lang: "zh-CN",
    title: "命令官",
    description: "An extension of the data pack system.",

    themeConfig: {
        sidebar: [
            {
                items: [
                    { text: 'Welcome!', link: '/zh-cn/' },
                    { text: 'Events', link: '/zh-cn/Events' },
                    { text: 'Commands', link: '/zh-cn/Commands' },
                    { text: 'Arithmetica', link: '/zh-cn/Arithmetica' }
                ]
            }
        ]
    }
})