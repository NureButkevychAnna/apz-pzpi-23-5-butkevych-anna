import requests


# Приклад використання офіційного YouTube Data API v3
# Демонструє взаємодію з серверною частиною архітектури YouTube


def get_video_info(video_id):
    """
    Отримує метадані відео через YouTube Data API.
    """

    url = "https://www.googleapis.com/youtube/v3/videos"

    params = {
        "id": video_id,  # ID відео (наприклад, dQw4w9WgXcQ)
        "part": "snippet,contentDetails,statistics",
        "key": "YOUR_API_KEY"  # API ключ Google Cloud
    }

    try:
        # Виконання HTTP GET запиту до серверів YouTube
        response = requests.get(url, params=params)
        response.raise_for_status()
        return response.json()

    except requests.exceptions.RequestException as e:
        return {"error": str(e)}


if __name__ == "__main__":
    # Тестовий запуск для отримання інформації про відео
    video_data = get_video_info("dQw4w9WgXcQ")
    print(video_data)