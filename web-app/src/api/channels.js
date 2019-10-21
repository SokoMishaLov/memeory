import axios from "axios";

export const getChannels = async () => {
    return await axios.get("channels/list/enabled");
}

export const getChannelLogoUrl = (channelId) => {
    return `${axios.defaults.baseURL}channels/logo/${channelId}`;
}