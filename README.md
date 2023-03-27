# ServerTools

## Requirements
- Java 11 or higher
- [JBang](https://www.jbang.dev)

## Install JBang
First you need to install [JBang](https://github.com/jbangdev/jbang), you can install using the command below or you can check more details directly in [JBang website](https://www.jbang.dev/download/).
```shell
curl -Ls https://sh.jbang.dev | bash -s - app setup
```

## CompressTool

The `compressTool` class is a Java command-line application that provides a user-friendly and flexible solution for scan a folder and compress files into a TGZ file, keeping separated compressed-files based on a given pattern.

### Install
With JBang intalled you can install `CompressTool`:
```shell
jbang app install https://raw.githubusercontent.com/ralexsander/ServerTools/main/compressTool.java
```

### Usage
```shell
compressTool --path=<path> --out=<path> [--preffix=<preffix>] [--suffix=<suffix>] [--before=<yyyy-MM-dd>] [--pattern=<yyyyMM>] [--delete=<delete>]

#### Parameters
- `--path`: The path location of files/backups.
- `--out`: The path location where TGZ will be saved.
- `--preffix`: If specified, only the files matching this preffix will be included.
- `--suffix`: If specified, only the files matching this suffix will be included.
- `--before`: Only consider files before this date.
- `--pattern`: Pattern of TGZ files. (default=yyyyMM)
- `--delete`: Removes the files after compression. (default=false)

#### Example
```shell
compressTool --path=/mnt/MyBackup/ --preffix "Invoice" --suffix ".xml" --out=/mnt/historic/ --before=2023-01-01
```

## ExpiredRemover

The `expiredRemover` class is a Java command-line application that provides a user-friendly and flexible solution for deleting old files based on specified criteria. The class can be executed from the command line and is designed to be easy to configure and use. It allows users to specify rules for selecting files to be removed, such as file age, file name and extension.

### Install
With JBang intalled you can install `ExpiredRemover`:
```shell
jbang app install https://raw.githubusercontent.com/ralexsander/ServerTools/main/expiredRemover.java
```

### Usage
```shell
expiredRemover --path=<path> [--preffix=<preffix>] [--suffix=<suffix>] [--keep-last=<last>] [--keep-daily=<daily>] [--keep-weekly=<weekly>] [--keep-monthly=<mon>] [--keep-yearly=<yearly>] [--dry-run=true]
```

#### Parameters
- `--path`: The path location of files/backups.
- `--preffix`: If specified, only the files matching this preffix will be included.
- `--suffix`: If specified, only the files matching this suffix will be included.
- `--keep-last`: Number of more recent files to keep. (default=1)
- `--keep-daily`: Number of more recent daily files to keep. (default=7)
- `--keep-weekly`: Number of more recent weekly files to keep. (default=5)
- `--keep-monthly`: Number of more recent monthly files to keep. (default=12)
- `--keep-yearly`: Number of more recent yearly files to keep. (default=5)
- `--dry-run`: Testing only, does not remove files. (default=true)

#### Example
In this example the files of path /mnt/MyBackup/ with name starting with `Bkp` and extension `.tar.gz` will be included. The script will keep the last 10 files, the last daily file for the last 30 days, a monthly file of past year and a yearly file for the last 10 years. No weekly files are keept.
```shell
expiredRemover --path=/mnt/MyBackup/ --preffix "Bkp" --suffix ".tar.gz" --keep-last=10 --keep-daily=30 --keep-weekly=0 --keep-monthly=12 --keep-yearly=10 --dry-run=true
```

## License
This project is licensed under the [MIT license](https://opensource.org/licenses/MIT). Feel free to use, modify, and distribute the code as you see fit.
