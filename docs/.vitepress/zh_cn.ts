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
                    { text: '表达式', link: '/zh-cn/develop/Expressions' }
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
            options: {
                locales: {
                    "zh-cn": {
                        translations: {
                            button: {
                                buttonText: '搜索文档',
                                buttonAriaLabel: '搜索文档'
                            },
                            modal: {
                                noResultsText: '无法找到相关结果',
                                resetButtonTitle: '清除查询条件',
                                footer: {
                                    selectText: '选择',
                                    navigateText: '切换'
                                }
                            }
                        }
                    }
                }
            }
        }
    }
})
