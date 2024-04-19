import { defineConfig } from 'vitepress'

export const zh_cn = defineConfig({
    lang: "zh-CN",
    title: "命令官",
    description: "对原版数据包系统的补充模组。",

    themeConfig: {
        sidebar: [
            {
                items: [
                    { text: '欢迎！', link: '/zh-cn/' }
                ]
            },
            {
                text: '使用',
                items: [
                    { text: '事件', link: '/zh-cn/Events' },
                    { text: '命令', link: '/zh-cn/Commands' },
                    { text: '表达式', link: '/zh-cn/Expressions' },
                ]
            },
            {
                text: '开发',
                items: [
                    { text: '事件', link: '/zh-cn/develop/Events'},
                    { text: '命令', link: '/zh-cn/develop/Commands'}
                ]
            },
            {
                text: 'Meta',
                items: [
                    { text: '设计', link: 'https://github.com/constellation-mc/commander/discussions/3' }
                ]
            }
        ],

        search: {
            provider: 'local',
        }
    }
})
