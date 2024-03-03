# URL Shortener

This project was created for me to learn Kotlin/Spring and refresh my React skills. You can try it out at https://s.ver.land.

![a screenshot of the application](screenshot.png)

## Running the project

1. Copy `.env.example` to `.env`, and set a value for `POSTGRES_PASSWORD`.
2. Run `./infra/pull-changes.sh` to pull the Docker image and start the web server and DB.

## Developing the project

1. Open the `api` directory in `IntelliJ IDEA`.
2. Install the `.env` extension.
3. Create a run config for `api/src/main/kotlin/land/ver/url_shortener/UrlShortenerApplication.kt`, specifying the `.env` file for environment variables.
4. Run `UrlShortenerApplication.kt`
5. Run `npm start` from the `ui` directory
6. Interact with the application from the opened browser.
