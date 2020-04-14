# Task 6 - Generics basics

[![Build Status](https://travis-ci.com/itmo-java-basics-2020/task6-generics-basics-<you-github>.svg?branch=master)](https://travis-ci.com/itmo-java-basics-2020/task6-generics-basics-<you-github>)

В данной лабораторной работе требуется *дженерифицировать* класс **ru.itmo.java.HashTable** написанный вами в лабораторной работе №4

Примером использования дженерифицированной версии ассоциативного массива может послужить следующая строка кода:

    HashTable<String, List<Integer> map = new HashTable<>();

Также у вас может возникнуть потребность в создании массива элементов дженерифицированного типа. Сделать это можно следующим образом:

    Pair<String>[] pairs = new Pair[10]

Ниже измененения интерфейса:

## Операция вставки/обновления

    V put(K key, V value);

## Операция поиска

    V get(K key);

## Операция удаления

    V remove(K key);

## Операция получения размера

    int size();