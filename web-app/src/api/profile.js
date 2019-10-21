import axios from "axios"
import _ from "lodash"
import {getMemeoryProfile, getSocialsMap, getToken, setAccount, setMemeoryProfile} from "../util/storage/storage";

export const saveProfile = async (profile = getMemeoryProfile()) => {

    const socialsMap = getSocialsMap()

    const savedProfile = await axios.post("profile/save", {
        id: _.get(profile, "id", getToken()),
        socialsMap: socialsMap,
        watchAllChannels: _.get(profile, "watchAllChannels", true),
        channels: _.get(profile, "channels", [])
    })

    _.forOwn(_.get(savedProfile, "socialsMap", {}), (value, key) => setAccount(key, value))
    setMemeoryProfile(savedProfile)

    return savedProfile
};

export const fetchProfile = async () => {
    return await axios.get("profile/get", {
        headers: {
            MEMEORY_TOKEN: getToken()
        }
    });
};
