begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|example
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|jms
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
import|;
end_import

begin_class
class|class
name|Listener
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|JMSException
block|{
specifier|final
name|String
name|TOPIC_PREFIX
init|=
literal|"topic://"
decl_stmt|;
name|String
name|user
init|=
name|env
argument_list|(
literal|"ACTIVEMQ_USER"
argument_list|,
literal|"admin"
argument_list|)
decl_stmt|;
name|String
name|password
init|=
name|env
argument_list|(
literal|"ACTIVEMQ_PASSWORD"
argument_list|,
literal|"password"
argument_list|)
decl_stmt|;
name|String
name|host
init|=
name|env
argument_list|(
literal|"ACTIVEMQ_HOST"
argument_list|,
literal|"localhost"
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|env
argument_list|(
literal|"ACTIVEMQ_PORT"
argument_list|,
literal|"5672"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|connectionURI
init|=
literal|"amqp://"
operator|+
name|host
operator|+
literal|":"
operator|+
name|port
decl_stmt|;
name|String
name|destinationName
init|=
name|arg
argument_list|(
name|args
argument_list|,
literal|0
argument_list|,
literal|"topic://event"
argument_list|)
decl_stmt|;
name|JmsConnectionFactory
name|factory
init|=
operator|new
name|JmsConnectionFactory
argument_list|(
name|connectionURI
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|(
name|user
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|destinationName
operator|.
name|startsWith
argument_list|(
name|TOPIC_PREFIX
argument_list|)
condition|)
block|{
name|destination
operator|=
name|session
operator|.
name|createTopic
argument_list|(
name|destinationName
operator|.
name|substring
argument_list|(
name|TOPIC_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|destination
operator|=
name|session
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
block|}
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|count
init|=
literal|1
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for messages..."
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|instanceof
name|TextMessage
condition|)
block|{
name|String
name|body
init|=
operator|(
operator|(
name|TextMessage
operator|)
name|msg
operator|)
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"SHUTDOWN"
operator|.
name|equals
argument_list|(
name|body
argument_list|)
condition|)
block|{
name|long
name|diff
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Received %d in %.2f seconds"
argument_list|,
name|count
argument_list|,
operator|(
literal|1.0
operator|*
name|diff
operator|/
literal|1000.0
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
if|if
condition|(
name|count
operator|!=
name|msg
operator|.
name|getIntProperty
argument_list|(
literal|"id"
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"mismatch: "
operator|+
name|count
operator|+
literal|"!="
operator|+
name|msg
operator|.
name|getIntProperty
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ignore
parameter_list|)
block|{                     }
if|if
condition|(
name|count
operator|==
literal|1
condition|)
block|{
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|count
operator|%
literal|1000
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Received %d messages."
argument_list|,
name|count
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Unexpected message type: "
operator|+
name|msg
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|String
name|env
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|String
name|rc
init|=
name|System
operator|.
name|getenv
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|==
literal|null
condition|)
return|return
name|defaultValue
return|;
return|return
name|rc
return|;
block|}
specifier|private
specifier|static
name|String
name|arg
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|int
name|index
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|index
operator|<
name|args
operator|.
name|length
condition|)
return|return
name|args
index|[
name|index
index|]
return|;
else|else
return|return
name|defaultValue
return|;
block|}
block|}
end_class

end_unit

