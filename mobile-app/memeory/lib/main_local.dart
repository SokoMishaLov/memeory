import 'package:memeory/util/env.dart';
import 'package:memeory/util/os.dart';

import 'app.dart';

void main() {
  BuildEnvironment.init(
    flavor: BuildFlavor.local,
    backendUrl: isAndroid() ? "http://10.0.2.2:8080" : "http://localhost:8080",
  );

  runMemeory();
}
