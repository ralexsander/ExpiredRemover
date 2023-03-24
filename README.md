# ExpiredRemover

This project is designed to automatically delete files that have expired based on a specified policy. The policy can be defined by the user.

## Requirements
- Java 11 or higher
- [JBang](https://www.jbang.dev)

## Install
First you need to install [JBang](https://github.com/jbangdev/jbang), you can install using the command below or you can check more details directly in [JBang website](https://www.jbang.dev/download/).
```shell
curl -Ls https://sh.jbang.dev | bash -s - app setup
```
With JBang intalled you can install `ExpiredRemover`:
```shell
jbang app install https://raw.githubusercontent.com/ralexsander/ExpiredRemover/main/expiredRemover.java
```

## Usage
```shell
expiredRemover --path=<path> [--preffix=<preffix>] [--suffix=<suffix>] [--keep-last=<last>] [--keep-daily=<daily>] [--keep-weekly=<weekly>] [--keep-monthly=<mon>] [--keep-yearly=<yearly>] [--dry-run=true]
```

### Parameters
- `--path`: The path location of files/backups.
- `--preffix`: If specified, only the files matching this preffix will be included.
- `--suffix`: If specified, only the files matching this suffix will be included.
- `--keep-last`: Number of more recent files to keep. (default=1)
- `--keep-daily`: Number of more recent daily files to keep. (default=7)
- `--keep-weekly`: Number of more recent weekly files to keep. (default=5)
- `--keep-monthly`: Number of more recent monthly files to keep. (default=12)
- `--keep-yearly`: Number of more recent yearly files to keep. (default=5)
- `--dry-run`: Testing only, does not remove files. (default=true)

### Example
In this example the files of path /mnt/MyBackup/ with name starting with `Bkp` and extension `.tar.gz` will be included. The script will keep the last 10 files, the last daily file for the last 30 days, a monthly file of past year and a yearly file for the last 10 years. No weekly files are keept.
```shell
expiredRemover --path=/mnt/MyBackup/ --preffix "Bkp" --suffix ".tar.gz" --keep-last=10 --keep-daily=30 --keep-weekly=0 --keep-monthly=12 --keep-yearly=10 --dry-run=true
```

## License
This project is licensed under the [MIT license](https://opensource.org/licenses/MIT). Feel free to use, modify, and distribute the code as you see fit.
