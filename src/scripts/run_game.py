from __future__ import annotations

import subprocess
import sys
from pathlib import Path


def main() -> int:
    project_dir = Path(__file__).resolve().parents[2]
    source_dir = project_dir / "src"
    bin_dir = project_dir / "bin"
    java_files = sorted(source_dir.rglob("*.java"))

    if not java_files:
        print("Keine Java-Dateien im src-Verzeichnis gefunden.", file=sys.stderr)
        return 1

    bin_dir.mkdir(exist_ok=True)
    compile_cmd = [
        "javac",
        "-encoding",
        "UTF-8",
        "-d",
        "bin",
        *[str(path.relative_to(project_dir)) for path in java_files],
    ]
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
        ["java", "-cp", "bin", "BattleshipGUI"],
        cwd=project_dir,
    )
    return run_result.returncode


if __name__ == "__main__":
    raise SystemExit(main())
