HIDRAW STOCK MANAGER - ANDROID PROJECT

APK இன்னும் build செய்யப்படவில்லை. GitHub Actions மூலம் APK build செய்யலாம்.

மொபைலில் இருந்து செய்யும் வழிமுறை:
1. ZIP-ஐ extract செய்யவும் (Files by Google போன்ற app பயன்படுத்தலாம்).
2. github.com-ல் account உருவாக்கி sign in செய்யவும்.
3. + > New repository > பெயர்: HIDRAW-Stock-Manager > Public அல்லது Private > Create repository.
4. Repository-ல் Add file > Upload files தேர்வு செய்து ZIP-ஐ extract செய்த folder-க்குள் உள்ள FILES-ஐ upload செய்யவும். ZIP file-ஐ மட்டும் upload செய்ய வேண்டாம்.
5. Commit changes செய்யவும்.
6. Actions tab திறக்கவும். Build Android APK workflow-ஐ தேர்வு செய்து Run workflow அழுத்தவும்.
7. Build முடிந்ததும் workflow run-ஐ திறந்து கீழே Artifacts பகுதியில் HIDRAW-Stock-Manager-APK download செய்யவும்.
8. ZIP artifact-ஐ extract செய்து app-debug.apk-ஐ திறந்து Install செய்யவும். Android கேட்டால் அந்த app-க்கு Install unknown apps அனுமதி கொடுக்கவும்.

குறிப்பு:
- இது debug APK; Play Store வெளியீட்டுக்கான signed release APK அல்ல.
- App தரவை இந்த மொபைலில் உள்ள local database-ல் சேமிக்கும். Uninstall / app data clear செய்தால் stock தரவு அழியலாம்.
- Physical stock check என்பது கணக்கிட்ட stock-ஐ உண்மையில் எண்ணிய stock-ஆக சரிசெய்யும்.
