import React from 'react'
import "./container.css"
import VideoAttachment from "./video/video-attachment"
import ImageAttachment from "./image/image-attachment"
import { timeAgo } from "../../../util/time/time"
import { ChannelLogo } from "../../common/logo/channel"
import _ from "lodash"
import { Button, Carousel, Dropdown, Menu } from "antd"
import { EllipsisOutlined } from "@ant-design/icons"
import { AspectRatio } from "react-aspect-ratio"
import { ATTACHMENT_TYPE, MEME_BORDER_RADIUS } from "../../../util/consts/consts"
import { withT } from "../../../util/locales/i18n";
import { errorToast, successToast } from "../../common/toast/toast";
import copy from "copy-to-clipboard";
import { PARAMS, ROUTE } from "../../../util/router/router";
import { isBrowser } from "react-device-detect"
import { withRouter } from "react-router";
import { stopPropagation } from "../../common/event/events";
import { connect } from "react-redux";
import { selectChannel } from "../../../store/selectors/channels";

const MemeContainer = ({t, history, meme, channel}) => {

    const renderItems = (attachments) => {
        const size = _.size(attachments)

        if (size > 1 && _.every(attachments, a => a["type"] === ATTACHMENT_TYPE.IMAGE)) {
            const items = _.map(attachments, renderAttachment)
            return <Carousel autoplay>{items}</Carousel>
        } else if (size === 1) {
            return renderAttachment(_.head(attachments))
        } else {
            return <div/>
        }
    }

    const renderAttachment = (a) => {
        const key = a["url"]
        switch (a["type"]) {
            case ATTACHMENT_TYPE.IMAGE:
                return <ImageAttachment key={key} attachment={a}/>
            case ATTACHMENT_TYPE.VIDEO:
                return <VideoAttachment key={key} attachment={a}/>
            case ATTACHMENT_TYPE.NONE:
            default:
                return <div key={key}/>
        }
    }

    const withAspectRatio = (component) => {
        const attachmentsAspectRatio = _.get(_.head(meme["attachments"]), "aspectRatio", 1.0)
        return (
            <AspectRatio ratio={attachmentsAspectRatio}
                         onClick={stopPropagation}
                         style={{
                             borderBottomRightRadius: MEME_BORDER_RADIUS,
                             borderBottomLeftRadius: MEME_BORDER_RADIUS,
                             overflow: "hidden"
                         }}>
                {component}
            </AspectRatio>
        );
    }

    const prepareMemeUri = () => {
        return ROUTE.MEMES_SINGLE.replace(PARAMS.ID, meme["id"])
    }

    const openSingleMeme = () => {
        history.push(prepareMemeUri())
    }

    const shareMeme = (e) => {
        stopPropagation(e)
        const link = `${window.location.origin.toString()}${process.env.PUBLIC_URL}${prepareMemeUri()}`
        copy(link)
        successToast(t("copied.to.clipboard"))
    }

    const report = (e) => {
        stopPropagation(e)
        errorToast(t("report.your.ass"))
    }

    return (
        <div className="meme"
             style={{
                 borderRadius: MEME_BORDER_RADIUS
             }}
             onClick={openSingleMeme}>
            <div className="meme-header">
                <div className="flex-space-between">
                    <div className="flex">
                        <ChannelLogo channelId={meme["channelId"]}/>
                        <div className="meme-header-channel">
                            <div className="meme-header-channel-name">
                                {_.get(channel, "name", "")}
                            </div>
                            <div className="meme-header-channel-ago">
                                {timeAgo(meme["publishedAt"])}
                            </div>
                        </div>
                    </div>
                </div>
                <div className="mr-10">
                    <Dropdown trigger={isBrowser ? ["hover", "click"] : ["click"]}
                              placement="bottomCenter"
                              overlay={
                                  <Menu>
                                      <Menu.Item key="0"
                                                 onClick={shareMeme}>
                                          {t("share.meme")}
                                      </Menu.Item>
                                      <Menu.Item key="1"
                                                 onClick={report}>
                                          {t("report.abuse")}
                                      </Menu.Item>
                                  </Menu>
                              }>

                        <Button icon={<EllipsisOutlined/>}
                                onClick={stopPropagation}
                                style={{
                                    background: "transparent"
                                }}/>
                    </Dropdown>
                </div>
            </div>

            <div className="meme-caption">
                {meme["caption"]}
            </div>


            {withAspectRatio(renderItems(meme["attachments"]))}
        </div>
    )
}

const mapStateToProps = (state, ownProps) => ({
    channel: selectChannel(state, _.get(ownProps, "meme.channelId"))
});

export default _.flow(
    withT,
    withRouter,
    connect(mapStateToProps)
)(MemeContainer)