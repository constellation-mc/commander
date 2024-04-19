import { defineConfig } from 'vitepress'

export const en = defineConfig({
    lang: "en-US",
    title: "Commander",
    description: "An extension of the data pack system.",

    themeConfig: {
        sidebar: [
            {
                items: [
                    { text: 'Welcome!', link: '/' }
                ]
            },
            {
                text: 'Use',
                items: [
                    { text: 'Events', link: '/Events' },
                    { text: 'Commands', link: '/Commands' },
                    { text: 'Expressions', link: '/Expressions' },
                ]
            },
            {
                text: 'Develop',
                items: [
                    { text: 'Events', link: '/develop/Events'},
                    { text: 'Commands', link: '/develop/Commands'}
                ]
            },
            {
                text: 'Meta',
                items: [
                    { text: 'Badges', link: 'https://github.com/constellation-mc/commander/discussions/3' }
                ]
            }
        ],

        search: {
            provider: 'local',
        }
    }
})