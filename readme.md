#heroDuel服务启动
nohup java -Dfile.encoding=UTF-8 -Dlog.home=/data/home/user00/log/match -Xloggc:/data/home/user00/log/match/heroDuel_gc.log -cp /data/home/user00/playcrab/war/version/octopus/target/Octopus-0.0.1-SNAPSHOT.jar com.playcrab.war.heroDuel.App -sc /data/home/user00/playcrab/war/version/octopus/boot/heroDuel.json -log /data/home/user00/playcrab/war/version/octopus/boot/logback.xml  > /dev/null 2>&1 &


#match服务启动（后台模式）
nohup java -Dfile.encoding=UTF-8 -Dlog.home=/data/home/user00/log/duel -Xloggc:/data/home/user00/log/duel/match_gc.log -cp /data/home/user00/playcrab/war/version/octopus/target/Octopus-0.0.1-SNAPSHOT.jar com.playcrab.war.match2P.App -sc /data/home/user00/playcrab/war/version/octopus/boot/heroDuelMatch.json -log /data/home/user00/playcrab/war/version/octopus/boot/logback.xml > /dev/null 2>&1 &
