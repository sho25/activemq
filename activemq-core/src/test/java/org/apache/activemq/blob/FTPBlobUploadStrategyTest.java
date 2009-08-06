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
name|blob
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|FileWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQSession
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
name|EmbeddedBrokerTestSupport
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
name|ActiveMQBlobMessage
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
name|MessageId
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
name|net
operator|.
name|ftp
operator|.
name|FTPClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ftpserver
operator|.
name|FtpServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ftpserver
operator|.
name|FtpServerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ftpserver
operator|.
name|ftplet
operator|.
name|AuthorizationRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ftpserver
operator|.
name|ftplet
operator|.
name|User
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ftpserver
operator|.
name|ftplet
operator|.
name|UserManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ftpserver
operator|.
name|listener
operator|.
name|ListenerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ftpserver
operator|.
name|usermanager
operator|.
name|PropertiesUserManagerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ftpserver
operator|.
name|usermanager
operator|.
name|UsernamePasswordAuthentication
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ftpserver
operator|.
name|usermanager
operator|.
name|impl
operator|.
name|BaseUser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|Expectations
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|Mockery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|api
operator|.
name|Invocation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|lib
operator|.
name|action
operator|.
name|CustomAction
import|;
end_import

begin_class
specifier|public
class|class
name|FTPBlobUploadStrategyTest
extends|extends
name|EmbeddedBrokerTestSupport
block|{
specifier|private
specifier|static
specifier|final
name|String
name|ftpServerListenerName
init|=
literal|"default"
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|FtpServer
name|server
decl_stmt|;
specifier|final
specifier|static
name|String
name|userNamePass
init|=
literal|"activemq"
decl_stmt|;
name|Mockery
name|context
init|=
literal|null
decl_stmt|;
name|String
name|ftpUrl
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|File
name|ftpHomeDirFile
init|=
operator|new
name|File
argument_list|(
literal|"target/FTPBlobTest/ftptest"
argument_list|)
decl_stmt|;
name|ftpHomeDirFile
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|ftpHomeDirFile
operator|.
name|getParentFile
argument_list|()
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|FtpServerFactory
name|serverFactory
init|=
operator|new
name|FtpServerFactory
argument_list|()
decl_stmt|;
name|ListenerFactory
name|factory
init|=
operator|new
name|ListenerFactory
argument_list|()
decl_stmt|;
name|PropertiesUserManagerFactory
name|userManagerFactory
init|=
operator|new
name|PropertiesUserManagerFactory
argument_list|()
decl_stmt|;
name|UserManager
name|userManager
init|=
name|userManagerFactory
operator|.
name|createUserManager
argument_list|()
decl_stmt|;
name|BaseUser
name|user
init|=
operator|new
name|BaseUser
argument_list|()
decl_stmt|;
name|user
operator|.
name|setName
argument_list|(
literal|"activemq"
argument_list|)
expr_stmt|;
name|user
operator|.
name|setPassword
argument_list|(
literal|"activemq"
argument_list|)
expr_stmt|;
name|user
operator|.
name|setHomeDirectory
argument_list|(
name|ftpHomeDirFile
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|userManager
operator|.
name|save
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|serverFactory
operator|.
name|setUserManager
argument_list|(
name|userManager
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setPort
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|serverFactory
operator|.
name|addListener
argument_list|(
name|ftpServerListenerName
argument_list|,
name|factory
operator|.
name|createListener
argument_list|()
argument_list|)
expr_stmt|;
name|server
operator|=
name|serverFactory
operator|.
name|createServer
argument_list|()
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|ftpPort
init|=
name|serverFactory
operator|.
name|getListener
argument_list|(
name|ftpServerListenerName
argument_list|)
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|ftpUrl
operator|=
literal|"ftp://"
operator|+
name|userNamePass
operator|+
literal|":"
operator|+
name|userNamePass
operator|+
literal|"@localhost:"
operator|+
name|ftpPort
operator|+
literal|"/ftptest/"
expr_stmt|;
name|bindAddress
operator|=
literal|"vm://localhost?jms.blobTransferPolicy.defaultUploadUrl="
operator|+
name|ftpUrl
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// check if file exist and delete it
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|ftpUrl
argument_list|)
decl_stmt|;
name|String
name|connectUrl
init|=
name|url
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|int
name|port
init|=
name|url
operator|.
name|getPort
argument_list|()
operator|<
literal|1
condition|?
literal|21
else|:
name|url
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|FTPClient
name|ftp
init|=
operator|new
name|FTPClient
argument_list|()
decl_stmt|;
name|ftp
operator|.
name|connect
argument_list|(
name|connectUrl
argument_list|,
name|port
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|ftp
operator|.
name|login
argument_list|(
literal|"activemq"
argument_list|,
literal|"activemq"
argument_list|)
condition|)
block|{
name|ftp
operator|.
name|quit
argument_list|()
expr_stmt|;
name|ftp
operator|.
name|disconnect
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Cant Authentificate to FTP-Server"
argument_list|)
throw|;
block|}
name|ftp
operator|.
name|changeWorkingDirectory
argument_list|(
literal|"ftptest"
argument_list|)
expr_stmt|;
name|ftp
operator|.
name|deleteFile
argument_list|(
literal|"testmessage"
argument_list|)
expr_stmt|;
name|ftp
operator|.
name|quit
argument_list|()
expr_stmt|;
name|ftp
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testFileUpload
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"amq-data-file-"
argument_list|,
literal|".dat"
argument_list|)
decl_stmt|;
comment|// lets write some data
name|BufferedWriter
name|writer
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|append
argument_list|(
literal|"hello world"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
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
operator|(
operator|(
name|ActiveMQConnection
operator|)
name|connection
operator|)
operator|.
name|setCopyMessageOnSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ActiveMQBlobMessage
name|message
init|=
call|(
name|ActiveMQBlobMessage
call|)
argument_list|(
operator|(
name|ActiveMQSession
operator|)
name|session
argument_list|)
operator|.
name|createBlobMessage
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|message
operator|.
name|setMessageId
argument_list|(
operator|new
name|MessageId
argument_list|(
literal|"testmessage"
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|onSend
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ftpUrl
operator|+
literal|"testmessage"
argument_list|,
name|message
operator|.
name|getURL
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

