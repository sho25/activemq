begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright 2005-2006 The Apache Software Foundation.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|web
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
name|ConnectionFactory
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
comment|/**  * A simple pool of JMS Session objects intended for use by Queue browsers.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|SessionPool
block|{
specifier|private
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|LinkedList
name|sessions
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
specifier|public
name|Connection
name|getConnection
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
name|connection
operator|=
name|getConnectionFactory
argument_list|()
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
return|return
name|connection
return|;
block|}
specifier|public
name|void
name|setConnection
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
block|}
specifier|public
name|ConnectionFactory
name|getConnectionFactory
parameter_list|()
block|{
if|if
condition|(
name|connectionFactory
operator|==
literal|null
condition|)
block|{
comment|// TODO support remote brokers too
name|connectionFactory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
expr_stmt|;
block|}
return|return
name|connectionFactory
return|;
block|}
specifier|public
name|void
name|setConnectionFactory
parameter_list|(
name|ConnectionFactory
name|connectionFactory
parameter_list|)
block|{
name|this
operator|.
name|connectionFactory
operator|=
name|connectionFactory
expr_stmt|;
block|}
specifier|public
name|Session
name|borrowSession
parameter_list|()
throws|throws
name|JMSException
block|{
name|Session
name|answer
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|sessions
init|)
block|{
if|if
condition|(
name|sessions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|answer
operator|=
name|createSession
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|answer
operator|=
operator|(
name|Session
operator|)
name|sessions
operator|.
name|removeLast
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|answer
return|;
block|}
specifier|protected
name|void
name|returnSession
parameter_list|(
name|Session
name|session
parameter_list|)
block|{
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|sessions
init|)
block|{
name|sessions
operator|.
name|add
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|Session
name|createSession
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getConnection
argument_list|()
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
return|;
block|}
block|}
end_class

end_unit

