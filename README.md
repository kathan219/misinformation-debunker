# 🧠 AI-Powered Real-Time Misinformation Debunker

A full-stack application that verifies misinformation in real time using large language models and fact-check APIs. Built with a scalable microservices-based architecture using **Spring Boot** (Java) and **React** (TypeScript), the platform allows users to input claims and receive fact-checked, AI-backed responses with confidence scores and trusted sources.

---

## 🌍 Live Preview

🚀 Coming Soon — Deployed on Vercel (Frontend) and Render/Fly.io (Backend)

---

## 🎯 Project Objective

Combat the spread of misinformation across the internet by offering:

- Real-time claim verification using multiple AI models (via OpenRouter)
- Cross-checking against reliable fact-check sources (Google Fact Check API, Snopes, etc.)
- A user-facing interface via a chatbot or browser extension
- Misinformation trend visualization and sentiment analysis

---

## 🏗️ High-Level Architecture

```
📦 misinformation-debunker/
├── frontend/       # React + TypeScript (User interface, charts, dashboard)
├── backend/        # Spring Boot (AI fact-check engine, aggregation logic)
├── .gitignore
└── README.md
```

---

## 🛠 Tech Stack

| Layer               | Technologies Used                                                                 |
|--------------------|------------------------------------------------------------------------------------|
| Frontend           | React, TypeScript, Tailwind CSS, Recharts                                         |
| Backend            | Java, Spring Boot, Spring AI, RestTemplate, Jackson                               |
| AI Models          | OpenRouter (GPT-4o-mini, DeepSeek, Gemma, Qwen, Mistral, etc.)                     |
| Fact-Check Sources | Google Fact Check API, Snopes, Politifact                                          |
| Visualization      | Chart.js / Recharts (for trends and sentiment analysis)                           |
| Caching/Storage    | Redis (optional), PostgreSQL, in-memory store (for MVP phase)                     |
| Monitoring         | Grafana, Prometheus (for admin dashboard, planned)                                |

---

## ✨ Key Features

- ✅ Verify claims using multiple LLMs with confidence scores
- ✅ AI-generated verdicts, explanations, sources, and reasoning
- ✅ Google Fact Check integration for real-time source aggregation
- ✅ Admin dashboard with trend analytics and sentiment charts
- ✅ Error boundaries and resilience against unreliable APIs
- ✅ Modular, container-ready structure for scalability

---

## ⚙️ Local Development

### Prerequisites

- Java 17+
- Node.js (v18+ recommended)
- Maven
- [OpenRouter API Key](https://openrouter.ai/)
- [Google Fact Check API Key](https://developers.google.com/fact-check/tools/api/reference/rest)

---

### 🧪 Backend (Spring Boot)

```bash
cd backend

# Set environment variables or configure application.properties
export OPENROUTER_API_KEY=your-key
export GOOGLE_API_KEY=your-key

# Run the backend server
./mvnw spring-boot:run
```

---

### 💻 Frontend (React)

```bash
cd frontend
npm install
npm run dev
```

> Ensure `proxy` in `frontend/package.json` is set to `http://localhost:8080` to call backend locally.

---

## 🔐 API Key Handling

API keys are **not committed** to source control.

- Keys are loaded using `${VAR}` in `application.properties`
- Secrets are passed via environment variables
- `.env` files or `.properties.local` files are **gitignored**

Example `application.properties`:
```properties
openrouter.api.key=${OPENROUTER_API_KEY}
google.api.key=${GOOGLE_API_KEY}
```

---

## 📈 Example Output

```json
{
  "verdict": "False",
  "confidence": "High",
  "explanation": "The claim is unsupported by peer-reviewed evidence.",
  "sources": [
    "Snopes: COVID-19 Vaccine Myths (https://snopes.com/...)", 
    "Science Feedback: Climate Change Facts (https://sciencefeedback.co/...)"
  ],
  "ai_thoughts": [
    "Step 1: Parsed the claim",
    "Step 2: Cross-referenced with fact-checking APIs",
    "Step 3: Confirmed lack of credible evidence"
  ]
}
```

---


## 📄 License

This project is licensed under the MIT License. See [`LICENSE`](./LICENSE) for details.

---

## 🙌 Credits

Built by Kathan — making the internet a more truthful place, one AI fact-check at a time.

Inspired by the work of OpenRouter, Google Fact Check API, and the open-source AI community.

---