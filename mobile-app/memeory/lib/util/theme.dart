import 'package:dynamic_theme/dynamic_theme.dart';
import 'package:flutter/material.dart';

ThemeData themeBuilder(Brightness brightness) {
  return brightness == Brightness.light
      ? ThemeData.light().copyWith(
          textTheme: ThemeData.light()
              .textTheme
              .apply(
                fontFamily: 'Memeory',
              )
              .copyWith(
                subtitle: TextStyle(
                  color: Colors.black.withOpacity(0.4),
                ),
              ),
          primaryColor: Colors.greenAccent,
          buttonTheme: ButtonThemeData(
            buttonColor: Colors.greenAccent,
            textTheme: ButtonTextTheme.primary,
          ),
        )
      : ThemeData.dark().copyWith(
          textTheme: ThemeData.dark()
              .textTheme
              .apply(
                fontFamily: 'Memeory',
              )
              .copyWith(
                subtitle: TextStyle(
                  color: Colors.white.withOpacity(0.6),
                ),
              ),
          errorColor: Colors.redAccent[200],
          accentColor: Colors.tealAccent,
          buttonTheme: ButtonThemeData(
            buttonColor: Colors.teal,
          ),
          primaryColorLight: Color.fromRGBO(26, 26, 27, 1),
          primaryColor: Color.fromRGBO(26, 26, 27, 1),
          primaryColorDark: Colors.black,
          toggleableActiveColor: Colors.teal[300],
        );
}

dependingOnThemeChoice({context, light, dark}) {
  return Theme.of(context).brightness == Brightness.light ? light : dark;
}

changeTheme(bool value, BuildContext context) {
  DynamicTheme.of(context).setBrightness(
    Theme.of(context).brightness == Brightness.dark
        ? Brightness.light
        : Brightness.dark,
  );
}
