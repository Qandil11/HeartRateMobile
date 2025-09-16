# ❤️ Heart Rate Monitoring App
![Build](https://github.com/Qandil11/HeartRateMobile/actions/workflows/android-ci.yml/badge.svg)
![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)

A **real-time heart rate monitoring Android app** built with **Kotlin** and **Jetpack Compose**, integrated with a **Python Flask backend** and **Google Cloud Firestore** for secure cloud-based data storage.  
This app allows users to measure, track, and analyze heart rate trends while receiving alerts for abnormal readings.

---

## ✨ Features

- 📲 **User Authentication** (Firebase Email/Password)
- ❤️ **Real-time Heart Rate Tracking** directly from the app
- ☁️ **Cloud Integration with Firestore** (secure storage of readings, thresholds, and reports)
- 📊 **Health Reports** with summaries, min/max/average readings
- 🔔 **Threshold Alerts**: get notified when heart rate is abnormal
- 📈 **Trends & Insights**: visualize data with charts and health zones
- 🔐 **Privacy First**: only processed data stored in Firestore; no external APIs

---

## 📸 Screenshots

<div>
<img width="230" height="406" alt="Picture 13" src="https://github.com/user-attachments/assets/e0056e6c-2e34-4aac-9b49-e59f3e035afe" />
<img width="222" height="389" alt="Picture 5" src="https://github.com/user-attachments/assets/a005bb9d-b437-40d5-9950-7166abc51b8e" />
<img width="226" height="396" alt="Picture 4" src="https://github.com/user-attachments/assets/baed9e3b-4a79-463a-a5d0-b9bd84dfeff3" />
<img width="226" height="396" alt="Picture 2" src="https://github.com/user-attachments/assets/dbf74d26-7b20-4539-b8b4-2dabbeab419d" />
<img width="226" height="396" alt="Picture 1" src="https://github.com/user-attachments/assets/2d6ddd32-5550-46ab-a57a-0debcc82ab16" />
</div>

---

## 🛠 Tech Stack

- **Frontend (Android)**: Kotlin, Jetpack Compose, MVVM, Coroutines
- **Backend**: Python (Flask microservices)
- **Cloud**: Google Cloud Firestore
- **Auth**: Firebase Authentication
- **Visualization**: Compose Charts

---

## ⚙️ Project Structure

```
app/          # Android app (Compose UI, ViewModels, Repositories)
server/       # Python Flask backend for API + Firestore integration
```

---

## 🚀 Getting Started

### Android App
```bash
# Clone repo
git clone https://github.com/Qandil11/HeartRateMobile.git
cd HeartRateMobile

# Build and run
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Server (Python Flask)
```bash
cd HeartRateServer
pip install -r requirements.txt
python app.py
```

---

## 🧪 Testing

- Unit tests included for data handling and alert logic.
```bash
./gradlew test
pytest server/tests
```

---

## 🗺 Roadmap

- [ ] Add push notifications for threshold alerts
- [ ] Integrate wearable device sensor APIs
- [ ] Add ML models for predictive heart health insights
- [ ] Deploy backend on Google Cloud Run

---

## 📜 License

```
MIT License
Copyright (c) 2025 Qandil Tariq
```

---

