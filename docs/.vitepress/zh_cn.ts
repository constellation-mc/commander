import { defineConfig } from 'vitepress'

export const zh_cn = defineConfig({
    lang: "zh-CN",
    title: "命令官",
    description: "对原版数据包系统的补充模组。",

    themeConfig: {
        sidebar: [
            {
                items: [
                    { text: '欢迎！', link: '/' }
                ]
            },
            {
                text: 'Use',
                items: [
                    { text: '事件', link: '/Events' },
                    { text: '命令', link: '/Commands' },
                    { text: '表达式', link: '/Expressions' },
                ]
            },
            {
                text: '开发',
                items: [
                    { text: '事件', link: '/develop/Events'},
                    { text: '命令', link: '/develop/Commands'}
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
