import { defineConfig } from 'vitepress'

export const zh_cn = defineConfig({
    lang: "zh-CN",
    title: "命令官",
    description: "对原版数据包系统的补充模组。",

    themeConfig: {
        sidebar: [
            {
                items: [
                    { text: '欢迎', link: '/zh-cn/' },
                    { text: '事件', link: '/zh-cn/Events' },
                    { text: '命令', link: '/zh-cn/Commands' },
                    { text: '算术', link: '/zh-cn/Arithmetica' }
                ]
            }
        ]
    }
})
