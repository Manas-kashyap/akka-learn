**Akka assignment - ```Manas Kashyap```**

Log files are stored under ```src/main/resources/log-files```

Dispatcher configuration is done under ```src/main/resources/application.conf```

Result is stored under ```src/main/resources/log-analysis-result```

____________AppDriver ```src/main/scala/com/knoldus/AppDriver``` is made (an object to run this application and to see whether its running correctly or not)____________

There are two case classes named `FileAnalysisResult` and `ScheduleMsg` for the display and storage or result and for the msg to be asked or tell to the actors

Trait is made of `utils` where my supervision strategy , writing to the log-analysis-result file and getting list of files is done

Analysis is the class where msg is asked about displaying result or cleaning the result file , it has different functions as per the requirement or the msg to display the result or clean it.

`It is scheduled for 5 min with the help of scheduler which can be found at the AppDriver.scala file `