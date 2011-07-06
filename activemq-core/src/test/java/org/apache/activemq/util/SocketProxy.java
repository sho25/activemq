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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
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
name|SocketException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketTimeoutException
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
name|ArrayList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|CountDownLatch
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
name|TimeUnit
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
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLServerSocket
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLServerSocketFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLSocketFactory
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

begin_class
specifier|public
class|class
name|SocketProxy
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SocketProxy
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|ACCEPT_TIMEOUT_MILLIS
init|=
literal|100
decl_stmt|;
specifier|private
name|URI
name|proxyUrl
decl_stmt|;
specifier|private
name|URI
name|target
decl_stmt|;
specifier|private
name|Acceptor
name|acceptor
decl_stmt|;
specifier|private
name|ServerSocket
name|serverSocket
decl_stmt|;
specifier|private
name|CountDownLatch
name|closed
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|public
name|List
argument_list|<
name|Bridge
argument_list|>
name|connections
init|=
operator|new
name|LinkedList
argument_list|<
name|Bridge
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|listenPort
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|receiveBufferSize
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|boolean
name|pauseAtStart
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|acceptBacklog
init|=
literal|50
decl_stmt|;
specifier|public
name|SocketProxy
parameter_list|()
throws|throws
name|Exception
block|{         }
specifier|public
name|SocketProxy
parameter_list|(
name|URI
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|this
argument_list|(
literal|0
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SocketProxy
parameter_list|(
name|int
name|port
parameter_list|,
name|URI
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|listenPort
operator|=
name|port
expr_stmt|;
name|target
operator|=
name|uri
expr_stmt|;
name|open
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setReceiveBufferSize
parameter_list|(
name|int
name|receiveBufferSize
parameter_list|)
block|{
name|this
operator|.
name|receiveBufferSize
operator|=
name|receiveBufferSize
expr_stmt|;
block|}
specifier|public
name|void
name|setTarget
parameter_list|(
name|URI
name|tcpBrokerUri
parameter_list|)
block|{
name|target
operator|=
name|tcpBrokerUri
expr_stmt|;
block|}
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|Exception
block|{
name|serverSocket
operator|=
name|createServerSocket
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|serverSocket
operator|.
name|setReuseAddress
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|receiveBufferSize
operator|>
literal|0
condition|)
block|{
name|serverSocket
operator|.
name|setReceiveBufferSize
argument_list|(
name|receiveBufferSize
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|proxyUrl
operator|==
literal|null
condition|)
block|{
name|serverSocket
operator|.
name|bind
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|listenPort
argument_list|)
argument_list|,
name|acceptBacklog
argument_list|)
expr_stmt|;
name|proxyUrl
operator|=
name|urlFromSocket
argument_list|(
name|target
argument_list|,
name|serverSocket
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|serverSocket
operator|.
name|bind
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|proxyUrl
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|acceptor
operator|=
operator|new
name|Acceptor
argument_list|(
name|serverSocket
argument_list|,
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
name|pauseAtStart
condition|)
block|{
name|acceptor
operator|.
name|pause
argument_list|()
expr_stmt|;
block|}
operator|new
name|Thread
argument_list|(
literal|null
argument_list|,
name|acceptor
argument_list|,
literal|"SocketProxy-Acceptor-"
operator|+
name|serverSocket
operator|.
name|getLocalPort
argument_list|()
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|closed
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|isSsl
parameter_list|(
name|URI
name|target
parameter_list|)
block|{
return|return
literal|"ssl"
operator|.
name|equals
argument_list|(
name|target
operator|.
name|getScheme
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|ServerSocket
name|createServerSocket
parameter_list|(
name|URI
name|target
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|isSsl
argument_list|(
name|target
argument_list|)
condition|)
block|{
return|return
name|SSLServerSocketFactory
operator|.
name|getDefault
argument_list|()
operator|.
name|createServerSocket
argument_list|()
return|;
block|}
return|return
operator|new
name|ServerSocket
argument_list|()
return|;
block|}
specifier|private
name|Socket
name|createSocket
parameter_list|(
name|URI
name|target
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|isSsl
argument_list|(
name|target
argument_list|)
condition|)
block|{
return|return
name|SSLSocketFactory
operator|.
name|getDefault
argument_list|()
operator|.
name|createSocket
argument_list|()
return|;
block|}
return|return
operator|new
name|Socket
argument_list|()
return|;
block|}
specifier|public
name|URI
name|getUrl
parameter_list|()
block|{
return|return
name|proxyUrl
return|;
block|}
comment|/*      * close all proxy connections and acceptor      */
specifier|public
name|void
name|close
parameter_list|()
block|{
name|List
argument_list|<
name|Bridge
argument_list|>
name|connections
decl_stmt|;
synchronized|synchronized
init|(
name|this
operator|.
name|connections
init|)
block|{
name|connections
operator|=
operator|new
name|ArrayList
argument_list|<
name|Bridge
argument_list|>
argument_list|(
name|this
operator|.
name|connections
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"close, numConnections="
operator|+
name|connections
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Bridge
name|con
range|:
name|connections
control|)
block|{
name|closeConnection
argument_list|(
name|con
argument_list|)
expr_stmt|;
block|}
name|acceptor
operator|.
name|close
argument_list|()
expr_stmt|;
name|closed
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
comment|/*      * close all proxy receive connections, leaving acceptor      * open      */
specifier|public
name|void
name|halfClose
parameter_list|()
block|{
name|List
argument_list|<
name|Bridge
argument_list|>
name|connections
decl_stmt|;
synchronized|synchronized
init|(
name|this
operator|.
name|connections
init|)
block|{
name|connections
operator|=
operator|new
name|ArrayList
argument_list|<
name|Bridge
argument_list|>
argument_list|(
name|this
operator|.
name|connections
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"halfClose, numConnections="
operator|+
name|connections
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Bridge
name|con
range|:
name|connections
control|)
block|{
name|halfCloseConnection
argument_list|(
name|con
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|waitUntilClosed
parameter_list|(
name|long
name|timeoutSeconds
parameter_list|)
throws|throws
name|InterruptedException
block|{
return|return
name|closed
operator|.
name|await
argument_list|(
name|timeoutSeconds
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
return|;
block|}
comment|/*      * called after a close to restart the acceptor on the same port      */
specifier|public
name|void
name|reopen
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"reopen"
argument_list|)
expr_stmt|;
try|try
block|{
name|open
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"exception on reopen url:"
operator|+
name|getUrl
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * pause accepting new connections and data transfer through existing proxy      * connections. All sockets remain open      */
specifier|public
name|void
name|pause
parameter_list|()
block|{
synchronized|synchronized
init|(
name|connections
init|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"pause, numConnections="
operator|+
name|connections
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|acceptor
operator|.
name|pause
argument_list|()
expr_stmt|;
for|for
control|(
name|Bridge
name|con
range|:
name|connections
control|)
block|{
name|con
operator|.
name|pause
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/*      * continue after pause      */
specifier|public
name|void
name|goOn
parameter_list|()
block|{
synchronized|synchronized
init|(
name|connections
init|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"goOn, numConnections="
operator|+
name|connections
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Bridge
name|con
range|:
name|connections
control|)
block|{
name|con
operator|.
name|goOn
argument_list|()
expr_stmt|;
block|}
block|}
name|acceptor
operator|.
name|goOn
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|closeConnection
parameter_list|(
name|Bridge
name|c
parameter_list|)
block|{
try|try
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"exception on close of: "
operator|+
name|c
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|halfCloseConnection
parameter_list|(
name|Bridge
name|c
parameter_list|)
block|{
try|try
block|{
name|c
operator|.
name|halfClose
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"exception on half close of: "
operator|+
name|c
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isPauseAtStart
parameter_list|()
block|{
return|return
name|pauseAtStart
return|;
block|}
specifier|public
name|void
name|setPauseAtStart
parameter_list|(
name|boolean
name|pauseAtStart
parameter_list|)
block|{
name|this
operator|.
name|pauseAtStart
operator|=
name|pauseAtStart
expr_stmt|;
block|}
specifier|public
name|int
name|getAcceptBacklog
parameter_list|()
block|{
return|return
name|acceptBacklog
return|;
block|}
specifier|public
name|void
name|setAcceptBacklog
parameter_list|(
name|int
name|acceptBacklog
parameter_list|)
block|{
name|this
operator|.
name|acceptBacklog
operator|=
name|acceptBacklog
expr_stmt|;
block|}
specifier|private
name|URI
name|urlFromSocket
parameter_list|(
name|URI
name|uri
parameter_list|,
name|ServerSocket
name|serverSocket
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|listenPort
init|=
name|serverSocket
operator|.
name|getLocalPort
argument_list|()
decl_stmt|;
return|return
operator|new
name|URI
argument_list|(
name|uri
operator|.
name|getScheme
argument_list|()
argument_list|,
name|uri
operator|.
name|getUserInfo
argument_list|()
argument_list|,
name|uri
operator|.
name|getHost
argument_list|()
argument_list|,
name|listenPort
argument_list|,
name|uri
operator|.
name|getPath
argument_list|()
argument_list|,
name|uri
operator|.
name|getQuery
argument_list|()
argument_list|,
name|uri
operator|.
name|getFragment
argument_list|()
argument_list|)
return|;
block|}
specifier|public
class|class
name|Bridge
block|{
specifier|private
name|Socket
name|receiveSocket
decl_stmt|;
specifier|private
name|Socket
name|sendSocket
decl_stmt|;
specifier|private
name|Pump
name|requestThread
decl_stmt|;
specifier|private
name|Pump
name|responseThread
decl_stmt|;
specifier|public
name|Bridge
parameter_list|(
name|Socket
name|socket
parameter_list|,
name|URI
name|target
parameter_list|)
throws|throws
name|Exception
block|{
name|receiveSocket
operator|=
name|socket
expr_stmt|;
name|sendSocket
operator|=
name|createSocket
argument_list|(
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
name|receiveBufferSize
operator|>
literal|0
condition|)
block|{
name|sendSocket
operator|.
name|setReceiveBufferSize
argument_list|(
name|receiveBufferSize
argument_list|)
expr_stmt|;
block|}
name|sendSocket
operator|.
name|connect
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|target
operator|.
name|getHost
argument_list|()
argument_list|,
name|target
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|linkWithThreads
argument_list|(
name|receiveSocket
argument_list|,
name|sendSocket
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"proxy connection "
operator|+
name|sendSocket
operator|+
literal|", receiveBufferSize="
operator|+
name|sendSocket
operator|.
name|getReceiveBufferSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|goOn
parameter_list|()
block|{
name|responseThread
operator|.
name|goOn
argument_list|()
expr_stmt|;
name|requestThread
operator|.
name|goOn
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|pause
parameter_list|()
block|{
name|requestThread
operator|.
name|pause
argument_list|()
expr_stmt|;
name|responseThread
operator|.
name|pause
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
synchronized|synchronized
init|(
name|connections
init|)
block|{
name|connections
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|receiveSocket
operator|.
name|close
argument_list|()
expr_stmt|;
name|sendSocket
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|halfClose
parameter_list|()
throws|throws
name|Exception
block|{
name|receiveSocket
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|linkWithThreads
parameter_list|(
name|Socket
name|source
parameter_list|,
name|Socket
name|dest
parameter_list|)
block|{
name|requestThread
operator|=
operator|new
name|Pump
argument_list|(
name|source
argument_list|,
name|dest
argument_list|)
expr_stmt|;
name|requestThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|responseThread
operator|=
operator|new
name|Pump
argument_list|(
name|dest
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|responseThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
class|class
name|Pump
extends|extends
name|Thread
block|{
specifier|protected
name|Socket
name|src
decl_stmt|;
specifier|private
name|Socket
name|destination
decl_stmt|;
specifier|private
name|AtomicReference
argument_list|<
name|CountDownLatch
argument_list|>
name|pause
init|=
operator|new
name|AtomicReference
argument_list|<
name|CountDownLatch
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|Pump
parameter_list|(
name|Socket
name|source
parameter_list|,
name|Socket
name|dest
parameter_list|)
block|{
name|super
argument_list|(
literal|"SocketProxy-DataTransfer-"
operator|+
name|source
operator|.
name|getPort
argument_list|()
operator|+
literal|":"
operator|+
name|dest
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|src
operator|=
name|source
expr_stmt|;
name|destination
operator|=
name|dest
expr_stmt|;
name|pause
operator|.
name|set
argument_list|(
operator|new
name|CountDownLatch
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|pause
parameter_list|()
block|{
name|pause
operator|.
name|set
argument_list|(
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|goOn
parameter_list|()
block|{
name|pause
operator|.
name|get
argument_list|()
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
try|try
block|{
name|InputStream
name|in
init|=
name|src
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|OutputStream
name|out
init|=
name|destination
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|len
init|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|==
operator|-
literal|1
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"read eof from:"
operator|+
name|src
argument_list|)
expr_stmt|;
break|break;
block|}
name|pause
operator|.
name|get
argument_list|()
operator|.
name|await
argument_list|()
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"read/write failed, reason: "
operator|+
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|receiveSocket
operator|.
name|isClosed
argument_list|()
condition|)
block|{
comment|// for halfClose, on read/write failure if we close the
comment|// remote end will see a close at the same time.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{                     }
block|}
block|}
block|}
block|}
specifier|public
class|class
name|Acceptor
implements|implements
name|Runnable
block|{
specifier|private
name|ServerSocket
name|socket
decl_stmt|;
specifier|private
name|URI
name|target
decl_stmt|;
specifier|private
name|AtomicReference
argument_list|<
name|CountDownLatch
argument_list|>
name|pause
init|=
operator|new
name|AtomicReference
argument_list|<
name|CountDownLatch
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|Acceptor
parameter_list|(
name|ServerSocket
name|serverSocket
parameter_list|,
name|URI
name|uri
parameter_list|)
block|{
name|socket
operator|=
name|serverSocket
expr_stmt|;
name|target
operator|=
name|uri
expr_stmt|;
name|pause
operator|.
name|set
argument_list|(
operator|new
name|CountDownLatch
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|socket
operator|.
name|setSoTimeout
argument_list|(
name|ACCEPT_TIMEOUT_MILLIS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|pause
parameter_list|()
block|{
name|pause
operator|.
name|set
argument_list|(
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|goOn
parameter_list|()
block|{
name|pause
operator|.
name|get
argument_list|()
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
operator|!
name|socket
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|pause
operator|.
name|get
argument_list|()
operator|.
name|await
argument_list|()
expr_stmt|;
try|try
block|{
name|Socket
name|source
init|=
name|socket
operator|.
name|accept
argument_list|()
decl_stmt|;
name|pause
operator|.
name|get
argument_list|()
operator|.
name|await
argument_list|()
expr_stmt|;
if|if
condition|(
name|receiveBufferSize
operator|>
literal|0
condition|)
block|{
name|source
operator|.
name|setReceiveBufferSize
argument_list|(
name|receiveBufferSize
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"accepted "
operator|+
name|source
operator|+
literal|", receiveBufferSize:"
operator|+
name|source
operator|.
name|getReceiveBufferSize
argument_list|()
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|connections
init|)
block|{
name|connections
operator|.
name|add
argument_list|(
operator|new
name|Bridge
argument_list|(
name|source
argument_list|,
name|target
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SocketTimeoutException
name|expected
parameter_list|)
block|{                     }
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"acceptor: finished for reason: "
operator|+
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
name|closed
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|goOn
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{             }
block|}
block|}
block|}
end_class

end_unit

