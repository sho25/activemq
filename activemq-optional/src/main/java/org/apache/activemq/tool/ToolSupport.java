begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
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
name|ActiveMQConnection
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
name|util
operator|.
name|IndentPrinter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_comment
comment|/**  * Abstract base class useful for implementation inheritence  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ToolSupport
block|{
specifier|protected
name|Destination
name|destination
decl_stmt|;
specifier|protected
name|String
name|subject
init|=
literal|"TOOL.DEFAULT"
decl_stmt|;
specifier|protected
name|boolean
name|topic
init|=
literal|true
decl_stmt|;
specifier|protected
name|String
name|user
init|=
name|ActiveMQConnection
operator|.
name|DEFAULT_USER
decl_stmt|;
specifier|protected
name|String
name|pwd
init|=
name|ActiveMQConnection
operator|.
name|DEFAULT_PASSWORD
decl_stmt|;
specifier|protected
name|String
name|url
init|=
name|ActiveMQConnection
operator|.
name|DEFAULT_BROKER_URL
decl_stmt|;
specifier|protected
name|boolean
name|transacted
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|durable
init|=
literal|false
decl_stmt|;
specifier|protected
name|String
name|clientID
init|=
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|protected
name|int
name|ackMode
init|=
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
decl_stmt|;
specifier|protected
name|String
name|consumerName
init|=
literal|"James"
decl_stmt|;
specifier|protected
name|Session
name|createSession
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|durable
condition|)
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|clientID
argument_list|)
expr_stmt|;
block|}
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
name|transacted
argument_list|,
name|ackMode
argument_list|)
decl_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|destination
operator|=
name|session
operator|.
name|createTopic
argument_list|(
name|subject
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
name|subject
argument_list|)
expr_stmt|;
block|}
return|return
name|session
return|;
block|}
specifier|protected
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|JMSException
throws|,
name|Exception
block|{
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|user
argument_list|,
name|pwd
argument_list|,
name|url
argument_list|)
decl_stmt|;
return|return
name|connectionFactory
operator|.
name|createConnection
argument_list|()
return|;
block|}
specifier|protected
name|void
name|close
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
comment|// lets dump the stats
name|dumpStats
argument_list|(
name|connection
argument_list|)
expr_stmt|;
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|dumpStats
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{
name|ActiveMQConnection
name|c
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connection
decl_stmt|;
name|c
operator|.
name|getConnectionStats
argument_list|()
operator|.
name|dump
argument_list|(
operator|new
name|IndentPrinter
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

