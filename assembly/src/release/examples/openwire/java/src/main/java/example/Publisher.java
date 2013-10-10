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
name|activemq
operator|.
name|ActiveMQConnectionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQTopic
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
name|JMSException
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
literal|"61616"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|destination
init|=
name|arg
argument_list|(
name|args
argument_list|,
literal|0
argument_list|,
literal|"event"
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
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://"
operator|+
name|host
operator|+
literal|":"
operator|+
name|port
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
name|dest
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
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
name|TextMessage
name|msg
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|body
argument_list|)
decl_stmt|;
name|msg
operator|.
name|setIntProperty
argument_list|(
literal|"id"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|i
operator|%
literal|1000
operator|)
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
literal|"Sent %d messages"
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"SHUTDOWN"
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
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

