#!/usr/bin/env bash

git tag -m "支持合约文本生成" v"$1"
git push origin v"$1"
gh release create v"$1" ./build/libs/dark-horse-tools-"$1".jar -F changelogs/changelog-"$1".md