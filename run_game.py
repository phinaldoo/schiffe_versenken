from __future__ import annotations

import subprocess
import sys
from pathlib import Path


def main() -> int:
    project_dir = Path(__file__).resolve().parent
    java_files = sorted(project_dir.glob("*.java"))

    if not java_files:
        print("Keine Java-Dateien im Projektverzeichnis gefunden.", file=sys.stderr)
        return 1

    compile_cmd = ["javac", "-encoding", "UTF-8", *[str(path.name) for path in java_files]]
    print("Kompiliere Java-Dateien ...")
    compile_result = subprocess.run(
        compile_cmd,
        cwd=project_dir,
        text=True,
    )
    if compile_result.returncode != 0:
        return compile_result.returncode

    print("Starte Main ...")
    run_result = subprocess.run(
        ["java", "BattleshipGUI"],
        cwd=project_dir,
    )
    return run_result.returncode


if __name__ == "__main__":
    raise SystemExit(main())
