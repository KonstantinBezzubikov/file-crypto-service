# gpb-file-crypto-service

Консольный сервис на Java 8 + Gradle без Spring Boot для пакетной шифровки/расшифровки файлов.

На текущем этапе модуль шифрования реализован как заглушка (`NoOpCryptoService`):
- принимает все параметры,
- корректно обрабатывает файлы и папки,
- поддерживает daemon-режим,
- резервирует файлы через суффикс `.in_progress`,
- создает выходные файлы с дефолтными именами,
- удаляет успешно обработанные исходники при обработке папки и в daemon-режиме,
- пропускает файлы `.enc`, `.dec`, `.in_progress`,
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

```bash
java -jar build/libs/gpb-file-crypto-service-1.0.0.jar -action encrypt -in /data/test.txt
```

Результат:
- входной файл на время обработки станет `/data/test.txt.in_progress`
- после обработки вернется обратно в `/data/test.txt`
- выходной файл будет `/data/test.txt.enc`

### Папка

```bash
java -jar build/libs/gpb-file-crypto-service-1.0.0.jar -action encrypt -in /data/inbox -out /data/outbox
```

Результат:
- все подходящие файлы из `/data/inbox` будут обработаны
- файлы `.enc`, `.dec`, `.in_progress` будут пропущены
- результирующие файлы попадут в `/data/outbox`
- успешно обработанные исходные файлы будут удалены
- если включена рекурсия, структура подпапок будет сохранена

### Daemon

```bash
java -jar build/libs/gpb-file-crypto-service-1.0.0.jar -action encrypt -mode daemon -in /data/inbox -out /data/outbox
```

Сервис будет циклически сканировать входную папку и удалять успешно обработанные исходники.

## Конфигурация

Базовый конфиг: `src/main/resources/application.properties`

CLI-аргументы имеют приоритет над конфигом.

## Сборка

```bash
gradle clean build
```

## Скрипты запуска

Каталог `scripts/` содержит готовые `.sh`-скрипты с комментариями для основных сценариев:
- `run-single-encrypt-default.sh`
- `run-single-decrypt-default.sh`
- `run-directory-encrypt-default.sh`
- `run-directory-decrypt-default.sh`
- `run-daemon-encrypt.sh`
- `run-daemon-decrypt.sh`
- `stop-daemon.sh`

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
