import 'package:flutter/material.dart';
import 'package:memeory/cache/repository/orientations_repo.dart';
import 'package:memeory/model/scrolling_axis.dart';
import 'package:memeory/pages/about/about.dart';
import 'package:memeory/pages/memes/memes_horizontal.dart';
import 'package:memeory/pages/memes/memes_vertical.dart';
import 'package:memeory/pages/preferences/widgets/channels.dart';
import 'package:memeory/pages/preferences/widgets/orientations.dart';
import 'package:memeory/pages/preferences/widgets/socials.dart';
import 'package:memeory/pages/preferences/widgets/wrapper.dart';
import 'package:memeory/util/consts/consts.dart';
import 'package:memeory/util/i18n/i18n.dart';
import 'package:memeory/util/theme/dark.dart';
import 'package:memeory/util/theme/light.dart';
import 'package:memeory/util/theme/theme.dart';
import 'package:page_transition/page_transition.dart';

class MemesPage extends StatelessWidget {
  const MemesPage({
    this.orientation = ScrollingAxis.VERTICAL,
  });

  final ScrollingAxis orientation;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: dependingOnThemeChoice(
        context: context,
        light: MEME_BACKGROUND_COLOR_LIGHT,
        dark: MEME_BACKGROUND_COLOR_DARK,
      ),
      appBar: PreferredSize(
        preferredSize: Size.fromHeight(50),
        child: AppBar(
          backgroundColor: dependingOnThemeChoice(
            context: context,
            light: APP_BAR_COLOR_LIGHT,
            dark: APP_BAR_COLOR_DARK,
          ),
          iconTheme: getDefaultIconThemeData(context),
          centerTitle: true,
          title: Text(
            t(context, "memes"),
            style: TextStyle(
              color: dependingOnThemeChoice(
                context: context,
                light: TEXT_COLOR_LIGHT,
                dark: TEXT_COLOR_DARK,
              ),
            ),
          ),
        ),
      ),
      drawer: Drawer(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            DrawerHeader(
              child: Stack(
                children: <Widget>[
                  GestureDetector(
                    onTap: () => Navigator.pop(context),
                    child: Icon(Icons.close),
                  ),
                  Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: <Widget>[
                        Container(
                          width: 70,
                          height: 70,
                          decoration: BoxDecoration(
                            shape: BoxShape.circle,
                            image: DecorationImage(
                              image: dependingOnThemeChoice(
                                context: context,
                                light: AssetImage(LOGO_ASSET),
                                dark: AssetImage(LOGO_INVERTED_ASSET),
                              ),
                            ),
                          ),
                        ),
                        Container(
                          padding: EdgeInsets.only(top: 10),
                          child: Text('Memeory!'),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            Expanded(
              child: ListView(
                padding: EdgeInsets.zero,
                children: <Widget>[
                  ListTile(
                    leading: Icon(Icons.person_outline),
                    title: Text(t(context, "profile")),
                    onTap: () {
                      pushToPrefs(
                        title: t(context, "please_authorize"),
                        context: context,
                        body: SocialPreferences(),
                      );
                    },
                  ),
                  ListTile(
                    leading: Icon(Icons.star_border),
                    title: Text(t(context, "channels")),
                    onTap: () {
                      pushToPrefs(
                        title: t(context, "choose_channels"),
                        context: context,
                        body: ChannelPreferences(),
                      );
                    },
                  ),
                  ListTile(
                    leading: Icon(Icons.rss_feed),
                    title: Text(t(context, "orientation")),
                    onTap: () {
                      pushToPrefs(
                        title: t(context, "choose_orientation"),
                        context: context,
                        body: OrientationPreferences(),
                      );
                    },
                  ),
                  ListTile(
                    leading: Icon(Icons.info_outline),
                    title: Text(t(context, "about_app")),
                    onTap: () {
                      pushToPrefs(
                        title: t(context, "about_app"),
                        context: context,
                        body: AboutApp(),
                      );
                    },
                  )
                ],
              ),
            ),
            Container(
              height: 50,
              margin: EdgeInsets.only(bottom: 20),
              child: SwitchListTile(
                title: Text(t(context, "theme_dark")),
                value: Theme.of(context).brightness == Brightness.dark,
                onChanged: (value) async {
                  Navigator.pop(context);
                  await changeTheme(context);
                },
              ),
            ),
          ],
        ),
      ),
      body: orientation == ScrollingAxis.VERTICAL
          ? MemesVertical()
          : MemesHorizontal(),
    );
  }

  void pushToPrefs({
    BuildContext context,
    String title,
    Widget body,
    Future apply,
  }) {
    Navigator.pop(context);
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (pageContext) => Scaffold(
          body: PreferencesPageWrapper(
            title: title ?? EMPTY,
            apply: () async {
              if (apply != null) await apply;
              var orientation = await getPreferredOrientation();

              Navigator.of(pageContext).pushReplacement(
                PageTransition(
                  type: PageTransitionType.leftToRightWithFade,
                  child: MemesPage(orientation: orientation),
                ),
              );
            },
            applyText: t(context, "back_to_watch_memes"),
            child: body,
          ),
        ),
      ),
    );
  }
}