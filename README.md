# Web-приложение для бронирования помещений/предметов - reservationApp

Приложение запускается через run в консоли sbt.

Первый запуск:
1) Необходимо создать БД в PostgresSQL (reservationAppDB).
2) В "conf/database.conf" (url, user, password) надо прописать необходимые данные для доступа к БД.
3) Зайти на localhost:9000, где необходимо, нажатием на красную кнопку, создать таблицы в БД. После этого должен появиться список routes.
4) Frontend загружается на localhost:3000
5) В начале существует только 1 аккаунт. Вход в него: E-mail - admin@admin.com, пароль - admin.
6) У администратора можно создать обычных пользователей.


Что использовалось (основные фреймворки и библиотеки):

BACKEND:
1) Play
2) PostgreSQL
3) Slick
4) Silhouette - библиотека-аутентификации для Play

FRONTEND:
1) React
2) Fullcalendar
3) Axios
4) Bootstrap
5) react-admin - библиотека для администрации приложения



Что реализовано:

FRONTEND:
1) Авторизация пользователей
2) Отображение событий на календаре
3) Создание, удаление событий
4) Страница профиля
5) Смена пароля
6) Header
7) Вывод данных о событии
8) Регистрация пользователей


BACKEND:
1) Добавление, удаление пользователей (users)
2) Добавление, изменение, удаление событий (events)
3) Добавление, изменение, удаление предметов (items)
4) Смена пароля
5) Авторизация и JWT-токен
6) CSRF-токен
7) Роли у пользователей и их изменение



Что НЕ реализовано:

FRONTEND:
1) Изменение событий 
2) Фильтр отображения событий на календаре
3) Выход при истёкшем JWT

Баги/недоработки:
1) У select нет default value.
2) Если нет AccessToken, должен быть Redirect на страницу входа (сейчас появляется ошибка) 
3) Отображение "00" минут (23:00 превращается в 22:59)
4) Все данные теущего пользователя выносятся в localStorage
5) При добавлении события, при очистке формы выходит ошибка "Заполните поле" в названии
6) В админке другой Header
7) Redirect "/" на /home, если выполнен вход

BACKEND:
1) Не реализовано обновление данных пользователя
2) При удалении пользователей они не удаляются из участников событий.
3) В таблице поле участников события - текстовое, а должно быть - json, из этого вытекает 2 пункт
4) Frontend не выключается с закрытием Backend - приходится завершать процесс (Node.js) вручную через "Диспетчер задач" (только если запускать через intelliJ Idea)
