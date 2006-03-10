begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|udp
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
name|command
operator|.
name|ActiveMQDestination
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
name|ActiveMQQueue
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
name|ActiveMQTextMessage
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
name|Command
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
name|ConsumerInfo
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
name|WireFormatInfo
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
name|transport
operator|.
name|Transport
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
name|transport
operator|.
name|TransportAcceptListener
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
name|transport
operator|.
name|TransportFactory
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
name|transport
operator|.
name|TransportListener
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
name|transport
operator|.
name|TransportServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageNotWriteableException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|UdpTestSupport
extends|extends
name|TestCase
implements|implements
name|TransportListener
block|{
specifier|protected
name|Transport
name|producer
decl_stmt|;
specifier|protected
name|Transport
name|consumer
decl_stmt|;
specifier|protected
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|protected
name|Command
name|receivedCommand
decl_stmt|;
specifier|protected
name|TransportServer
name|server
decl_stmt|;
specifier|protected
name|boolean
name|large
decl_stmt|;
specifier|public
name|void
name|testSendingSmallMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|ConsumerInfo
name|expected
init|=
operator|new
name|ConsumerInfo
argument_list|()
decl_stmt|;
name|expected
operator|.
name|setSelector
argument_list|(
literal|"Cheese"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setExclusive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setCommandId
argument_list|(
operator|(
name|short
operator|)
literal|12
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setExclusive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setPrefetchSize
argument_list|(
literal|3456
argument_list|)
expr_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"About to send: "
operator|+
name|expected
argument_list|)
expr_stmt|;
name|producer
operator|.
name|oneway
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|Command
name|received
init|=
name|assertCommandReceived
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have received a ConsumerInfo but was: "
operator|+
name|received
argument_list|,
name|received
operator|instanceof
name|ConsumerInfo
argument_list|)
expr_stmt|;
name|ConsumerInfo
name|actual
init|=
operator|(
name|ConsumerInfo
operator|)
name|received
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Selector"
argument_list|,
name|expected
operator|.
name|getSelector
argument_list|()
argument_list|,
name|actual
operator|.
name|getSelector
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"isExclusive"
argument_list|,
name|expected
operator|.
name|isExclusive
argument_list|()
argument_list|,
name|actual
operator|.
name|isExclusive
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getCommandId"
argument_list|,
name|expected
operator|.
name|getCommandId
argument_list|()
argument_list|,
name|actual
operator|.
name|getCommandId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getPrefetchSize"
argument_list|,
name|expected
operator|.
name|getPrefetchSize
argument_list|()
argument_list|,
name|actual
operator|.
name|getPrefetchSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Caught: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Failed to send to transport: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testSendingMediumMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
name|createMessageBodyText
argument_list|(
literal|4
operator|*
literal|105
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Foo.Bar.Medium"
argument_list|)
decl_stmt|;
name|assertSendTextMessage
argument_list|(
name|destination
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSendingLargeMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
name|createMessageBodyText
argument_list|(
literal|4
operator|*
literal|1024
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Foo.Bar.Large"
argument_list|)
decl_stmt|;
name|assertSendTextMessage
argument_list|(
name|destination
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertSendTextMessage
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|MessageNotWriteableException
block|{
name|large
operator|=
literal|true
expr_stmt|;
name|ActiveMQTextMessage
name|expected
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|expected
operator|.
name|setText
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"About to send message of type: "
operator|+
name|expected
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|producer
operator|.
name|oneway
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|Command
name|received
init|=
name|assertCommandReceived
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have received a ActiveMQTextMessage but was: "
operator|+
name|received
argument_list|,
name|received
operator|instanceof
name|ActiveMQTextMessage
argument_list|)
expr_stmt|;
name|ActiveMQTextMessage
name|actual
init|=
operator|(
name|ActiveMQTextMessage
operator|)
name|received
decl_stmt|;
name|assertEquals
argument_list|(
literal|"getDestination"
argument_list|,
name|expected
operator|.
name|getDestination
argument_list|()
argument_list|,
name|actual
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getText"
argument_list|,
name|expected
operator|.
name|getText
argument_list|()
argument_list|,
name|actual
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Received text message with: "
operator|+
name|actual
operator|.
name|getText
argument_list|()
operator|.
name|length
argument_list|()
operator|+
literal|" character(s)"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Caught: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Failed to send to transport: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|String
name|createMessageBodyText
parameter_list|(
name|int
name|loopSize
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
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
name|loopSize
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"0123456789"
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|server
operator|=
name|createServer
argument_list|()
expr_stmt|;
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|setAcceptListener
argument_list|(
operator|new
name|TransportAcceptListener
argument_list|()
block|{
specifier|public
name|void
name|onAccept
parameter_list|(
name|Transport
name|transport
parameter_list|)
block|{
name|consumer
operator|=
name|transport
expr_stmt|;
name|consumer
operator|.
name|setTransportListener
argument_list|(
name|UdpTestSupport
operator|.
name|this
argument_list|)
expr_stmt|;
try|try
block|{
name|consumer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
name|onAcceptError
parameter_list|(
name|Exception
name|error
parameter_list|)
block|{                 }
block|}
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|consumer
operator|=
name|createConsumer
argument_list|()
expr_stmt|;
if|if
condition|(
name|consumer
operator|!=
literal|null
condition|)
block|{
name|consumer
operator|.
name|setTransportListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|producer
operator|=
name|createProducer
argument_list|()
expr_stmt|;
name|producer
operator|.
name|setTransportListener
argument_list|(
operator|new
name|TransportListener
argument_list|()
block|{
specifier|public
name|void
name|onCommand
parameter_list|(
name|Command
name|command
parameter_list|)
block|{             }
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{             }
specifier|public
name|void
name|transportInterupted
parameter_list|()
block|{             }
specifier|public
name|void
name|transportResumed
parameter_list|()
block|{             }
block|}
argument_list|)
expr_stmt|;
name|producer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|producer
operator|!=
literal|null
condition|)
block|{
name|producer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|consumer
operator|!=
literal|null
condition|)
block|{
name|consumer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onCommand
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
if|if
condition|(
name|command
operator|instanceof
name|WireFormatInfo
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Got WireFormatInfo: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|large
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"### Received command: "
operator|+
name|command
operator|.
name|getClass
argument_list|()
operator|+
literal|" with id: "
operator|+
name|command
operator|.
name|getCommandId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"### Received command: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|receivedCommand
operator|=
name|command
expr_stmt|;
name|lock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"### Received error: "
operator|+
name|error
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|transportInterupted
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"### Transport interrupted"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|transportResumed
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"### Transport resumed"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Command
name|assertCommandReceived
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|Command
name|answer
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|answer
operator|=
name|receivedCommand
expr_stmt|;
if|if
condition|(
name|answer
operator|==
literal|null
condition|)
block|{
name|lock
operator|.
name|wait
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
name|answer
operator|=
name|receivedCommand
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
literal|"Should have received a Command by now!"
argument_list|,
name|answer
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
specifier|abstract
name|Transport
name|createConsumer
parameter_list|()
throws|throws
name|Exception
function_decl|;
specifier|protected
specifier|abstract
name|Transport
name|createProducer
parameter_list|()
throws|throws
name|Exception
function_decl|;
specifier|protected
name|TransportServer
name|createServer
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

