import ru from "./langs/ru.json";
import en from "./langs/en.json";
import i18next from 'i18next';
import {initReactI18next, withTranslation} from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';
import {unAwait} from "../http/http";
import detectBrowserLanguage from 'detect-browser-language'

unAwait(i18next
    .use(LanguageDetector)
    .use(initReactI18next)
    .init({
        fallbackLng: 'en-US',
        defaultNS: "memeory",
        nsSeparator: ':::',
        keySeparator: '::',
        resources: {
            "en-US": {
                memeory: en
            },
            "ru-RU": {
                memeory: ru
            },
        },
        react: {
            wait: true
        }
    }))

export const withT = (component) => withTranslation("memeory")(component)

export const BROWSER_LANGUAGE = detectBrowserLanguage()

// noinspection JSUnusedGlobalSymbols
export default i18next