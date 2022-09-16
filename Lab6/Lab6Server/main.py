import sqlite3
from flask import Flask, request, json


api = Flask(__name__)
db = sqlite3.connect('game_stats.db', check_same_thread=False)


@api.route('/', methods=['GET'])
def get_leaderboard():
    ret = ""
    sort_type = request.args.get("sort_by")

    data = db.execute(f"SELECT * FROM statistics ORDER BY {sort_type} DESC")
    for item in data:
        ret += f"{item[0]},{item[1]},{item[2]},{item[3]};"

    return ret


@api.route('/', methods=['POST'])
def post_data():
    post_nickname = request.form.get("nickname")
    post_score = int(request.form.get("score"))
    post_time = int(request.form.get("time"))

    try:
        sql = 'INSERT INTO statistics (nickname, score, time) values(?, ?, ?)'
        db.execute(sql, (post_nickname, post_score, post_time))
        db.commit()
    except Exception as e:
        print(e)
        return json.dumps('{"success": false, "reason": "Exception! Check server logs."}')

    return json.dumps('{"success": true}')


if __name__ == '__main__':
    db.execute("CREATE TABLE IF NOT EXISTS statistics (id INTEGER PRIMARY KEY AUTOINCREMENT, nickname TEXT, score INTEGER, time INTEGER);")
    api.run(host="0.0.0.0")
