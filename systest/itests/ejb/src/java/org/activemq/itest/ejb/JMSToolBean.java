begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 Hiram Chirino  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|itest
operator|.
name|ejb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|RemoteException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ejb
operator|.
name|EJBException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ejb
operator|.
name|SessionBean
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ejb
operator|.
name|SessionContext
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
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
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
name|javax
operator|.
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|InitialContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * This is a SSB that uses an outbound JMS Resource Adapter.  *  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|JMSToolBean
implements|implements
name|SessionBean
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|3834596495499474741L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JMSToolBean
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|SessionContext
name|sessionContext
decl_stmt|;
specifier|private
name|Context
name|envContext
decl_stmt|;
specifier|public
name|void
name|ejbCreate
parameter_list|()
block|{     }
specifier|public
name|void
name|ejbRemove
parameter_list|()
block|{     }
specifier|public
name|void
name|ejbActivate
parameter_list|()
block|{     }
specifier|public
name|void
name|ejbPassivate
parameter_list|()
block|{     }
specifier|public
name|void
name|setSessionContext
parameter_list|(
name|SessionContext
name|sessionContext
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|sessionContext
operator|=
name|sessionContext
expr_stmt|;
name|envContext
operator|=
operator|(
name|Context
operator|)
operator|new
name|InitialContext
argument_list|()
operator|.
name|lookup
argument_list|(
literal|"java:comp/env"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EJBException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|sendTextMessage
parameter_list|(
name|String
name|dest
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|RemoteException
throws|,
name|JMSException
throws|,
name|NamingException
block|{
name|sendTextMessage
argument_list|(
name|createDestination
argument_list|(
name|dest
argument_list|)
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|sendTextMessage
parameter_list|(
name|Destination
name|dest
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|RemoteException
throws|,
name|JMSException
throws|,
name|NamingException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"sendTextMessage start"
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
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
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|log
operator|.
name|info
argument_list|(
literal|"sendTextMessage end"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|receiveTextMessage
parameter_list|(
name|String
name|dest
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|RemoteException
throws|,
name|JMSException
throws|,
name|NamingException
block|{
return|return
name|receiveTextMessage
argument_list|(
name|createDestination
argument_list|(
name|dest
argument_list|)
argument_list|,
name|timeout
argument_list|)
return|;
block|}
specifier|public
name|String
name|receiveTextMessage
parameter_list|(
name|Destination
name|dest
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|RemoteException
throws|,
name|JMSException
throws|,
name|NamingException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"receiveTextMessage start"
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
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
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|TextMessage
name|message
init|=
literal|null
decl_stmt|;
name|message
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
return|return
name|message
operator|==
literal|null
condition|?
literal|null
else|:
name|message
operator|.
name|getText
argument_list|()
return|;
block|}
finally|finally
block|{
name|log
operator|.
name|info
argument_list|(
literal|"receiveTextMessage end"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|drain
parameter_list|(
name|String
name|dest
parameter_list|)
throws|throws
name|RemoteException
throws|,
name|JMSException
throws|,
name|NamingException
block|{
return|return
name|drain
argument_list|(
name|createDestination
argument_list|(
name|dest
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|drain
parameter_list|(
name|Destination
name|dest
parameter_list|)
throws|throws
name|RemoteException
throws|,
name|JMSException
throws|,
name|NamingException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"drain start"
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
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
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|counter
operator|++
expr_stmt|;
block|}
return|return
name|counter
return|;
block|}
finally|finally
block|{
name|log
operator|.
name|info
argument_list|(
literal|"drain end"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|Destination
name|createDestination
parameter_list|(
name|String
name|dest
parameter_list|)
throws|throws
name|NamingException
block|{
return|return
operator|(
name|Destination
operator|)
name|envContext
operator|.
name|lookup
argument_list|(
name|dest
argument_list|)
return|;
block|}
specifier|private
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|NamingException
throws|,
name|JMSException
block|{
name|ConnectionFactory
name|cf
init|=
operator|(
name|ConnectionFactory
operator|)
name|envContext
operator|.
name|lookup
argument_list|(
literal|"jms/Default"
argument_list|)
decl_stmt|;
name|Connection
name|con
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
return|return
name|con
return|;
block|}
block|}
end_class

end_unit

