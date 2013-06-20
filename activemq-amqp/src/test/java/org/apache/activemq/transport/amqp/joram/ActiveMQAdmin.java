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
name|transport
operator|.
name|amqp
operator|.
name|joram
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
name|broker
operator|.
name|BrokerFactory
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
name|broker
operator|.
name|BrokerService
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
name|broker
operator|.
name|TransportConnector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|admin
operator|.
name|Admin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|amqp_1_0
operator|.
name|jms
operator|.
name|impl
operator|.
name|ConnectionFactoryImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|amqp_1_0
operator|.
name|jms
operator|.
name|impl
operator|.
name|QueueImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|amqp_1_0
operator|.
name|jms
operator|.
name|impl
operator|.
name|TopicImpl
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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQAdmin
implements|implements
name|Admin
block|{
name|Context
name|context
decl_stmt|;
block|{
comment|// enableJMSFrameTracing();
try|try
block|{
comment|// Use the jetty JNDI context since it's mutable.
specifier|final
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
init|=
operator|new
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|env
operator|.
name|put
argument_list|(
literal|"java.naming.factory.initial"
argument_list|,
literal|"org.eclipse.jetty.jndi.InitialContextFactory"
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
literal|"java.naming.factory.url.pkgs"
argument_list|,
literal|"org.eclipse.jetty.jndi"
argument_list|)
expr_stmt|;
empty_stmt|;
name|context
operator|=
operator|new
name|InitialContext
argument_list|(
name|env
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|static
specifier|public
name|void
name|enableJMSFrameTracing
parameter_list|()
block|{
try|try
block|{
specifier|final
name|SimpleFormatter
name|formatter
init|=
operator|new
name|SimpleFormatter
argument_list|()
decl_stmt|;
name|String
name|outputStreamName
init|=
literal|"amqp-trace.txt"
decl_stmt|;
specifier|final
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|outputStreamName
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Handler
name|handler
init|=
operator|new
name|Handler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|publish
parameter_list|(
name|LogRecord
name|r
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s:%s"
argument_list|,
name|r
operator|.
name|getLoggerName
argument_list|()
argument_list|,
name|r
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
block|{
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|SecurityException
block|{                 }
block|}
decl_stmt|;
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
literal|"FRM"
argument_list|)
decl_stmt|;
name|log
operator|.
name|addHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|log
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|FINEST
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker://()/localhost?persistent=false"
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|static
name|BrokerService
name|broker
decl_stmt|;
specifier|static
name|int
name|port
decl_stmt|;
specifier|public
name|void
name|startServer
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|stopServer
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"basedir"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"basedir"
argument_list|,
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|TransportConnector
name|connector
init|=
name|broker
operator|.
name|addConnector
argument_list|(
literal|"amqp://localhost:0"
argument_list|)
decl_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|port
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stopServer
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|Context
name|createContext
parameter_list|()
throws|throws
name|NamingException
block|{
return|return
name|context
return|;
block|}
specifier|public
name|void
name|createQueue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
name|context
operator|.
name|bind
argument_list|(
name|name
argument_list|,
operator|new
name|QueueImpl
argument_list|(
literal|"queue://"
operator|+
name|name
argument_list|)
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|createTopic
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
name|context
operator|.
name|bind
argument_list|(
name|name
argument_list|,
operator|new
name|TopicImpl
argument_list|(
literal|"topic://"
operator|+
name|name
argument_list|)
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|deleteQueue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// BrokerTestSupport.delete_queue((Broker)base.broker, name);
try|try
block|{
name|context
operator|.
name|unbind
argument_list|(
name|name
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|deleteTopic
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
name|context
operator|.
name|unbind
argument_list|(
name|name
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|createConnectionFactory
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
specifier|final
name|ConnectionFactory
name|factory
init|=
operator|new
name|ConnectionFactoryImpl
argument_list|(
literal|"localhost"
argument_list|,
name|port
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|context
operator|.
name|bind
argument_list|(
name|name
argument_list|,
name|factory
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|deleteConnectionFactory
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
name|context
operator|.
name|unbind
argument_list|(
name|name
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|createQueueConnectionFactory
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|createConnectionFactory
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|createTopicConnectionFactory
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|createConnectionFactory
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|deleteQueueConnectionFactory
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|deleteConnectionFactory
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|deleteTopicConnectionFactory
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|deleteConnectionFactory
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

