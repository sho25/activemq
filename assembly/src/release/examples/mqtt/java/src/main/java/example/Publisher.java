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
name|fusesource
operator|.
name|hawtbuf
operator|.
name|AsciiBuffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|hawtbuf
operator|.
name|Buffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|hawtbuf
operator|.
name|UTF8Buffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|client
operator|.
name|Future
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|client
operator|.
name|FutureConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|client
operator|.
name|MQTT
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|client
operator|.
name|QoS
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_comment
comment|/**  * Uses a Future based API to MQTT.  */
end_comment

begin_class
class|class
name|Publisher
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
name|Exception
block|{
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
literal|"1883"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|destination
init|=
name|arg
argument_list|(
name|args
argument_list|,
literal|0
argument_list|,
literal|"/topic/event"
argument_list|)
decl_stmt|;
name|int
name|messages
init|=
literal|10000
decl_stmt|;
name|int
name|size
init|=
literal|256
decl_stmt|;
name|String
name|DATA
init|=
literal|"abcdefghijklmnopqrstuvwxyz"
decl_stmt|;
name|String
name|body
init|=
literal|""
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|body
operator|+=
name|DATA
operator|.
name|charAt
argument_list|(
name|i
operator|%
name|DATA
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Buffer
name|msg
init|=
operator|new
name|AsciiBuffer
argument_list|(
name|body
argument_list|)
decl_stmt|;
name|MQTT
name|mqtt
init|=
operator|new
name|MQTT
argument_list|()
decl_stmt|;
name|mqtt
operator|.
name|setHost
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setUserName
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|FutureConnection
name|connection
init|=
name|mqtt
operator|.
name|futureConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|connect
argument_list|()
operator|.
name|await
argument_list|()
expr_stmt|;
specifier|final
name|LinkedList
argument_list|<
name|Future
argument_list|<
name|Void
argument_list|>
argument_list|>
name|queue
init|=
operator|new
name|LinkedList
argument_list|<
name|Future
argument_list|<
name|Void
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|UTF8Buffer
name|topic
init|=
operator|new
name|UTF8Buffer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|messages
condition|;
name|i
operator|++
control|)
block|{
comment|// Send the publish without waiting for it to complete. This allows us
comment|// to send multiple message without blocking..
name|queue
operator|.
name|add
argument_list|(
name|connection
operator|.
name|publish
argument_list|(
name|topic
argument_list|,
name|msg
argument_list|,
name|QoS
operator|.
name|AT_LEAST_ONCE
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// Eventually we start waiting for old publish futures to complete
comment|// so that we don't create a large in memory buffer of outgoing message.s
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>=
literal|1000
condition|)
block|{
name|queue
operator|.
name|removeFirst
argument_list|()
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|i
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
literal|"Sent %d messages."
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|queue
operator|.
name|add
argument_list|(
name|connection
operator|.
name|publish
argument_list|(
name|topic
argument_list|,
operator|new
name|AsciiBuffer
argument_list|(
literal|"SHUTDOWN"
argument_list|)
argument_list|,
name|QoS
operator|.
name|AT_LEAST_ONCE
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|queue
operator|.
name|removeFirst
argument_list|()
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
name|connection
operator|.
name|disconnect
argument_list|()
operator|.
name|await
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
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

