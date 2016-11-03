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
name|tcp
package|;
end_package

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
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|SocketFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  *  *  * Automatically generated socket.close() calls to simulate network faults  */
end_comment

begin_class
specifier|public
class|class
name|SocketTstFactory
extends|extends
name|SocketFactory
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SocketTstFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ConcurrentMap
argument_list|<
name|InetAddress
argument_list|,
name|Integer
argument_list|>
name|closeIter
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|InetAddress
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
class|class
name|SocketTst
block|{
specifier|private
class|class
name|Bagot
implements|implements
name|Runnable
block|{
specifier|private
specifier|final
name|Thread
name|processus
decl_stmt|;
specifier|private
specifier|final
name|Socket
name|socket
decl_stmt|;
specifier|private
specifier|final
name|InetAddress
name|address
decl_stmt|;
specifier|public
name|Bagot
parameter_list|(
name|Random
name|rnd
parameter_list|,
name|Socket
name|socket
parameter_list|,
name|InetAddress
name|address
parameter_list|)
block|{
name|this
operator|.
name|processus
operator|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|,
literal|"Network Faults maker : undefined"
argument_list|)
expr_stmt|;
name|this
operator|.
name|socket
operator|=
name|socket
expr_stmt|;
name|this
operator|.
name|address
operator|=
name|address
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
block|{
name|this
operator|.
name|processus
operator|.
name|setName
argument_list|(
literal|"Network Faults maker : "
operator|+
name|this
operator|.
name|socket
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|processus
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|int
name|lastDelayVal
decl_stmt|;
name|Integer
name|lastDelay
decl_stmt|;
while|while
condition|(
operator|!
name|this
operator|.
name|processus
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|socket
operator|.
name|isClosed
argument_list|()
condition|)
block|{
try|try
block|{
name|lastDelay
operator|=
name|closeIter
operator|.
name|get
argument_list|(
name|this
operator|.
name|address
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastDelay
operator|==
literal|null
condition|)
block|{
name|lastDelayVal
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|lastDelayVal
operator|=
name|lastDelay
operator|.
name|intValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|lastDelayVal
operator|>
literal|10
condition|)
name|lastDelayVal
operator|+=
literal|20
expr_stmt|;
else|else
name|lastDelayVal
operator|+=
literal|1
expr_stmt|;
block|}
name|lastDelay
operator|=
operator|new
name|Integer
argument_list|(
name|lastDelayVal
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Trying to close client socket "
operator|+
name|socket
operator|.
name|toString
argument_list|()
operator|+
literal|" in "
operator|+
name|lastDelayVal
operator|+
literal|" milliseconds"
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|lastDelayVal
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|this
operator|.
name|processus
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{                             }
name|this
operator|.
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
name|closeIter
operator|.
name|put
argument_list|(
name|this
operator|.
name|address
argument_list|,
name|lastDelay
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Client socket "
operator|+
name|this
operator|.
name|socket
operator|.
name|toString
argument_list|()
operator|+
literal|" is closed."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{                         }
block|}
name|this
operator|.
name|processus
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|final
name|Bagot
name|bagot
decl_stmt|;
specifier|private
specifier|final
name|Socket
name|socket
decl_stmt|;
specifier|public
name|SocketTst
parameter_list|(
name|InetAddress
name|address
parameter_list|,
name|int
name|port
parameter_list|,
name|Random
name|rnd
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|socket
operator|=
operator|new
name|Socket
argument_list|(
name|address
argument_list|,
name|port
argument_list|)
expr_stmt|;
name|bagot
operator|=
operator|new
name|Bagot
argument_list|(
name|rnd
argument_list|,
name|this
operator|.
name|socket
argument_list|,
name|address
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SocketTst
parameter_list|(
name|InetAddress
name|address
parameter_list|,
name|int
name|port
parameter_list|,
name|InetAddress
name|localAddr
parameter_list|,
name|int
name|localPort
parameter_list|,
name|Random
name|rnd
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|socket
operator|=
operator|new
name|Socket
argument_list|(
name|address
argument_list|,
name|port
argument_list|,
name|localAddr
argument_list|,
name|localPort
argument_list|)
expr_stmt|;
name|bagot
operator|=
operator|new
name|Bagot
argument_list|(
name|rnd
argument_list|,
name|this
operator|.
name|socket
argument_list|,
name|address
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SocketTst
parameter_list|(
name|String
name|address
parameter_list|,
name|int
name|port
parameter_list|,
name|Random
name|rnd
parameter_list|)
throws|throws
name|UnknownHostException
throws|,
name|IOException
block|{
name|this
operator|.
name|socket
operator|=
operator|new
name|Socket
argument_list|(
name|address
argument_list|,
name|port
argument_list|)
expr_stmt|;
name|bagot
operator|=
operator|new
name|Bagot
argument_list|(
name|rnd
argument_list|,
name|this
operator|.
name|socket
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
name|address
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SocketTst
parameter_list|(
name|String
name|address
parameter_list|,
name|int
name|port
parameter_list|,
name|InetAddress
name|localAddr
parameter_list|,
name|int
name|localPort
parameter_list|,
name|Random
name|rnd
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|socket
operator|=
operator|new
name|Socket
argument_list|(
name|address
argument_list|,
name|port
argument_list|,
name|localAddr
argument_list|,
name|localPort
argument_list|)
expr_stmt|;
name|bagot
operator|=
operator|new
name|Bagot
argument_list|(
name|rnd
argument_list|,
name|this
operator|.
name|socket
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
name|address
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Socket
name|getSocket
parameter_list|()
block|{
return|return
name|this
operator|.
name|socket
return|;
block|}
specifier|public
name|void
name|startBagot
parameter_list|()
block|{
name|bagot
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
empty_stmt|;
specifier|private
specifier|final
name|Random
name|rnd
decl_stmt|;
specifier|public
name|SocketTstFactory
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating a new SocketTstFactory"
argument_list|)
expr_stmt|;
name|this
operator|.
name|rnd
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|InetAddress
name|host
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
name|SocketTst
name|sockTst
decl_stmt|;
name|sockTst
operator|=
operator|new
name|SocketTst
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|this
operator|.
name|rnd
argument_list|)
expr_stmt|;
name|sockTst
operator|.
name|startBagot
argument_list|()
expr_stmt|;
return|return
name|sockTst
operator|.
name|getSocket
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|InetAddress
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|InetAddress
name|localAddress
parameter_list|,
name|int
name|localPort
parameter_list|)
throws|throws
name|IOException
block|{
name|SocketTst
name|sockTst
decl_stmt|;
name|sockTst
operator|=
operator|new
name|SocketTst
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|localAddress
argument_list|,
name|localPort
argument_list|,
name|this
operator|.
name|rnd
argument_list|)
expr_stmt|;
name|sockTst
operator|.
name|startBagot
argument_list|()
expr_stmt|;
return|return
name|sockTst
operator|.
name|getSocket
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
name|SocketTst
name|sockTst
decl_stmt|;
name|sockTst
operator|=
operator|new
name|SocketTst
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|this
operator|.
name|rnd
argument_list|)
expr_stmt|;
name|sockTst
operator|.
name|startBagot
argument_list|()
expr_stmt|;
return|return
name|sockTst
operator|.
name|getSocket
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|InetAddress
name|localAddress
parameter_list|,
name|int
name|localPort
parameter_list|)
throws|throws
name|IOException
block|{
name|SocketTst
name|sockTst
decl_stmt|;
name|sockTst
operator|=
operator|new
name|SocketTst
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|localAddress
argument_list|,
name|localPort
argument_list|,
name|this
operator|.
name|rnd
argument_list|)
expr_stmt|;
name|sockTst
operator|.
name|startBagot
argument_list|()
expr_stmt|;
return|return
name|sockTst
operator|.
name|getSocket
argument_list|()
return|;
block|}
specifier|private
specifier|final
specifier|static
name|SocketTstFactory
name|client
init|=
operator|new
name|SocketTstFactory
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|SocketFactory
name|getDefault
parameter_list|()
block|{
return|return
name|client
return|;
block|}
block|}
end_class

end_unit

