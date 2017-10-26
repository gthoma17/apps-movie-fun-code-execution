ourDatabase=$(echo "show databases;" | cf mysql movies-mysql | grep cf_ )
echo "drop database $ourDatabase; create database $ourDatabase" | cf mysql movies-mysql

