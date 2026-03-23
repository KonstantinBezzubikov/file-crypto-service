# file-crypto-service

Консольный сервис на Java 8 + Gradle без Spring Boot для пакетной шифровки/расшифровки файлов.

На текущем этапе модуль шифрования реализован как заглушка (`NoOpCryptoService`):
- принимает все параметры,
- корректно обрабатывает файлы и папки,
- поддерживает daemon-режим,
- резервирует файлы через суффикс `.in_progress`,
- создает выходные файлы с дефолтными именами,
- готов к замене на реальную криптографию.

## Возможности

- `-action encrypt|decrypt`
- `-mode default|daemon`
- `-in <файл|папка>`
- `-out <файл|папка>`
- `-crl-file <путь>`
- `-conf <properties>`
- работа с одним файлом
- работа с папкой
- дефолтные выходные имена:
    - шифрование: `<имя>.enc`
    - расшифровка: `<имя>.dec`
- daemon-режим со сканированием папки
- безопасная параллельная обработка за счет временного переименования входного файла в `*.in_progress`

## Поведение

### Один файл

Пример:

```bash
java -jar build/libs/file-crypto-service-1.0.0.jar -action encrypt -in /data/test.txt
```

Результат:
- входной файл на время обработки станет `/data/test.txt.in_progress`
- после обработки вернется обратно в `/data/test.txt`
- выходной файл будет `/data/test.txt.enc`

### Папка

Пример:

```bash
java -jar build/libs/file-crypto-service-1.0.0.jar -action decrypt -in /data/inbox -out /data/outbox
```

Результат:
- все файлы из `/data/inbox` будут обработаны
- результирующие файлы попадут в `/data/outbox`
- если включена рекурсия, структура подпапок будет сохранена

### Daemon

Пример:

```bash
java -jar build/libs/file-crypto-service-1.0.0.jar -action encrypt -mode daemon -in /data/inbox -out /data/outbox
```

Сервис будет циклически сканировать входную папку.

## Конфигурация

Базовый конфиг: `src/main/resources/application.properties`

Пример внешнего конфига:

```properties
app.action=encrypt
app.mode=daemon
app.in=/opt/data/inbox
app.out=/opt/data/outbox
app.scanIntervalMs=5000
app.recursive=true
app.includeHidden=false
app.shutdownWaitMs=3000
app.inProgressSuffix=.in_progress
```

CLI-аргументы имеют приоритет над конфигом.

## Сборка

```bash
gradle clean build
```

или через wrapper, если вы его добавите в свой контур.

## Запуск

```bash
java -jar build/libs/file-crypto-service-1.0.0.jar -action encrypt -mode default -in /tmp/a.txt
```

## Архитектура

- `Application` — точка входа
- `CommandLineParser` — разбор аргументов
- `ConfigLoader` — загрузка и валидация конфигурации
- `ProcessingCoordinator` — координация режимов запуска
- `SingleRunProcessor` — обработка файла/папки за один проход
- `DirectoryDaemon` — циклическое сканирование папки
- `FileCollector` — сбор файлов в каталоге
- `FileReservationService` — резервирование файла через `.in_progress`
- `OutputPathResolver` — расчет результирующих путей
- `CryptoService` — контракт криптомодуля
- `NoOpCryptoService` — временная заглушка вместо реального шифрования

## Следующий этап

Чтобы встроить реальное шифрование, достаточно заменить `NoOpCryptoService` на боевую реализацию `CryptoService`.
