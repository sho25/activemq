begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|JMSException
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
name|ActiveMQConnection
import|;
end_import

begin_comment
comment|/**  * A JMS 1.1 log4j appender which uses ActiveMQ by default and does not require  * any JNDI configurations  *   *   */
end_comment

begin_class
specifier|public
class|class
name|JmsLogAppender
extends|extends
name|JmsLogAppenderSupport
block|{
specifier|private
name|String
name|uri
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
specifier|private
name|String
name|userName
decl_stmt|;
specifier|private
name|String
name|password
decl_stmt|;
specifier|public
name|JmsLogAppender
parameter_list|()
block|{     }
specifier|public
name|String
name|getUri
parameter_list|()
block|{
return|return
name|uri
return|;
block|}
specifier|public
name|void
name|setUri
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
block|}
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|userName
return|;
block|}
specifier|public
name|void
name|setUserName
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|this
operator|.
name|userName
operator|=
name|userName
expr_stmt|;
block|}
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
specifier|public
name|void
name|setPassword
parameter_list|(
name|String
name|password
parameter_list|)
block|{
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
block|}
specifier|protected
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|userName
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|ActiveMQConnection
operator|.
name|makeConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|,
name|uri
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Unable to connect to a broker using "
operator|+
literal|"userName: \'"
operator|+
name|userName
operator|+
literal|"\' password \'"
operator|+
name|password
operator|+
literal|"\' uri \'"
operator|+
name|uri
operator|+
literal|"\' :: error - "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
else|else
block|{
try|try
block|{
return|return
name|ActiveMQConnection
operator|.
name|makeConnection
argument_list|(
name|uri
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Unable to connect to a broker using "
operator|+
literal|"uri \'"
operator|+
name|uri
operator|+
literal|"\' :: error - "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

