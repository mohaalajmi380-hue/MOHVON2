# خطة حل مشكلة اتصال VPN وتجهيز مشروع Flutter للتصدير

حل مشكلة عدم استجابة زر **CONNECT** عند النقر عليه في المحاكي، وتوفير حزمة مشروع **Flutter** متكاملة وقابلة للتصدير مع دعم WireGuard و Kill Switch.

---

### مراجعة المستخدم والقرارات المؤكدة

> [!IMPORTANT]
> **القرارات المؤكدة بناءً على إجاباتك:**
> 1. **إصلاح زر الاتصال في المحاكي فوراً**: زر الـ CONNECT كان يحصر النقر داخل مساحة داخلية صغيرة بدون مؤشر لمس (Ripple / Visual Tap Indication) وبدون رسائل Snackbar تفاعلية، مما يعطي انطباعاً بأنه "لا يحدث شيء". سنقوم بتوسيع منطقة النقر لتشمل كامل الدائرة النيونية (230dp)، مع إضافة رد فعل فوري عند الضغط وصوت/اهتزاز ولمعان، ورسالة تفاعلية تبين حالة مصافحة WireGuard.
> 2. **تجهيز مشروع Flutter كامل للتصدير**: إنشاء ملفات مشروع Flutter المهيكل بنمط Clean Architecture والجاهز للتصدير والتجميع الفوري (`pubspec.yaml`, `main.dart`, `vpn_engine`, `method_channels`, و واجهات الدارك سايبربانك النيونية) ليتمكن المستخدم من تحميله وتشغيله على أجهزة Android و iOS الحقيقية عبر WireGuardKit و WireGuard Android Tunnel.

---

## 1. نظرة عامة والهدف

1. **في بيئة المحاكي الحالية (Android Jetpack Compose)**:
   - تمكين تفاعل فوري فائق الاستجابة عند الضغط على أي جزء من زر الـ Cyber Connect الدائري.
   - إظهار حالة المصافحة اللحظية (Handshake)، وتوليد مفاتيح التشفير التلقائي، وتغيير الـ IP فوراً إلى الـ IP الآمن المشفر، وتفعيل العدادات الحية للسرعة (Upload / Download) وزمن الاتصال.
   - إضافة إشعار سريع (Snackbar / Toast HUD) يوضح للمستخدم: *"تم بدء نفق WireGuard المشفر بنجاح"*.

2. **حزمة تصدير مشروع Flutter (Flutter Export Package)**:
   - توفير ملفات Flutter الكاملة في مجلد مخصص للمشروع (`flutter_project/`) يحتوي على:
     - `pubspec.yaml` مع كافة التبعيات الضرورية ومكتبات إدارة الحالة.
     - هيكلية Clean Architecture (Presentation, Domain, Core, MethodChannels).
     - شاشات الاتصال، اختيار الخوادم، وإعدادات الـ Kill Switch والـ Split Tunneling.
     - أكواد Android (`MainActivity.kt` + `VpnService`) وأكواد iOS (`PacketTunnelProvider.swift`).

---

## 2. المخطط المعماري وتدفق الاتصال

```
┌──────────────────────────────────────────────────────────────┐
│                    واجهة المستخدم (HUD UI)                   │
│      [زر النيون 230dp] ◄── نقر المستخدم (One-Tap Trigger)     │
└──────────────────────────────┬───────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────┐
│             إدارة الحالة (VpnViewModel / BLoC)               │
│  DISCONNECTED ──► CONNECTING (Handshake) ──► CONNECTED       │
│  - توليد مفاتيح ChaCha20-Poly1305                           │
│  - استبدال الـ IP المكشوف بالـ IP المشفر                      │
│  - بدء حلقات قياس سرعة الرفع/التحميل الحية                   │
└──────────────────────────────┬───────────────────────────────┘
                               │
               ┌───────────────┴───────────────┐
               ▼                               ▼
┌─────────────────────────────┐ ┌─────────────────────────────┐
│  المحاكي المباشر (Compose)   │ │  تصدير Flutter (Production) │
│ - رد فعل فوري للمس          │ │ - MethodChannel للـ Native   │
│ - انيميشن دوران وهالة ضوئية │ │ - Android VpnService Go      │
│ - عداد زمني وسرعات حقيقية    │ │ - iOS PacketTunnelProvider   │
└─────────────────────────────┘ └─────────────────────────────┘
```

---

## 3. خطوات التنفيذ المجدولة

### المرحلة الأولى: إصلاح استجابة الزر في التطبيق الحالي (Android Compose)
1. تعديل `CyberButton.kt`:
   - توسيع منطقة النقر (`Modifier.clickable`) لتغطي كامل الحاوية الدائرية بقطر 220dp مع توفير `ripple` بصري وتأثير ضغط ناعم (`scale on press`).
   - إضافة صوت أو إشعار فوري عند الضغط يوضح أن الاتصال بدأ فعلاً دون أي تأخير في إدراك المستخدم.
2. تعديل `DashboardScreen.kt`:
   - إظهار شريط تنبيهي سفلي (Cyber HUD Banner) عند بدء الاتصال يخبر المستخدم بحالة بروتوكول WireGuard.
   - ضمان استجابة فورية بدون أي تجميد.

### المرحلة الثانية: إعداد حزمة كود Flutter الكاملة للتصدير
1. إنشاء دليل ومجلد مشروع Flutter متكامل في الجذر يحتوي على:
   - `flutter_mohvpn/pubspec.yaml`
   - `flutter_mohvpn/lib/main.dart`
   - `flutter_mohvpn/lib/features/dashboard/presentation/screens/dashboard_screen.dart`
   - `flutter_mohvpn/lib/features/vpn_engine/data/wireguard_channel.dart`
   - `flutter_mohvpn/lib/features/servers/presentation/screens/server_list_screen.dart`
   - إرشادات التصدير والتشغيل عبر `flutter run` أو تصدير الـ APK.

### المرحلة الثالثة: التحقق والاختبار
- تجميع الكود والتأكد من نجاح الـ build وخلوّه من أي أخطاء.
- فحص عمل النقر في المحاكي وتأكيد الانتقال السلس للحالات (Disconnected -> Connecting -> Connected).
