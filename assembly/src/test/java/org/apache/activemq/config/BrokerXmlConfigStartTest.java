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
name|config
package|;
end_package

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
name|FileFilter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|Collection
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
name|Properties
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
name|TimeoutException
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|stomp
operator|.
name|StompConnection
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
name|URISupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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
annotation|@
name|RunWith
argument_list|(
name|value
operator|=
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|BrokerXmlConfigStartTest
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
name|BrokerXmlConfigStartTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|Properties
name|secProps
decl_stmt|;
specifier|private
name|String
name|configUrl
decl_stmt|;
specifier|private
name|String
name|shortName
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"{1}"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|String
index|[]
argument_list|>
name|getTestParameters
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
index|[]
argument_list|>
name|configUrls
init|=
operator|new
name|ArrayList
argument_list|<
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|configUrls
operator|.
name|add
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"xbean:src/release/conf/activemq.xml"
block|,
literal|"activemq.xml"
block|}
argument_list|)
expr_stmt|;
name|String
name|osName
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"os.name {} "
argument_list|,
name|osName
argument_list|)
expr_stmt|;
name|File
name|sampleConfDir
init|=
operator|new
name|File
argument_list|(
literal|"target/conf"
argument_list|)
decl_stmt|;
name|String
name|sampleConfDirPath
init|=
name|sampleConfDir
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
if|if
condition|(
name|osName
operator|.
name|toLowerCase
argument_list|()
operator|.
name|contains
argument_list|(
literal|"windows"
argument_list|)
condition|)
block|{
name|sampleConfDirPath
operator|=
name|sampleConfDirPath
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// Chop off drive letter and :
name|sampleConfDirPath
operator|=
name|sampleConfDirPath
operator|.
name|replace
argument_list|(
literal|"\\"
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|File
name|xmlFile
range|:
name|sampleConfDir
operator|.
name|listFiles
argument_list|(
operator|new
name|FileFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|pathname
parameter_list|)
block|{
return|return
name|pathname
operator|.
name|isFile
argument_list|()
operator|&&
name|pathname
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"activemq-"
argument_list|)
operator|&&
name|pathname
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"xml"
argument_list|)
return|;
block|}
block|}
argument_list|)
control|)
block|{
name|configUrls
operator|.
name|add
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"xbean:"
operator|+
name|sampleConfDirPath
operator|+
literal|"/"
operator|+
name|xmlFile
operator|.
name|getName
argument_list|()
block|,
name|xmlFile
operator|.
name|getName
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|configUrls
return|;
block|}
specifier|public
name|BrokerXmlConfigStartTest
parameter_list|(
name|String
name|config
parameter_list|,
name|String
name|configFileShortName
parameter_list|)
block|{
name|this
operator|.
name|configUrl
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|shortName
operator|=
name|configFileShortName
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStartBrokerUsingXmlConfig1
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
literal|null
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker config: "
operator|+
name|configUrl
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Broker config: "
operator|+
name|configUrl
argument_list|)
expr_stmt|;
name|broker
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
name|configUrl
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"activemq-leveldb-replicating.xml"
operator|.
name|equals
argument_list|(
name|shortName
argument_list|)
condition|)
block|{
try|try
block|{
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|expectedWithNoZk
parameter_list|)
block|{
return|return;
block|}
block|}
else|else
block|{
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// alive, now try connect to connect
try|try
block|{
for|for
control|(
name|TransportConnector
name|transport
range|:
name|broker
operator|.
name|getTransportConnectors
argument_list|()
control|)
block|{
specifier|final
name|URI
name|UriToConnectTo
init|=
name|URISupport
operator|.
name|removeQuery
argument_list|(
name|transport
operator|.
name|getConnectUri
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|UriToConnectTo
operator|.
name|getScheme
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"stomp"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"validating alive with connection to: "
operator|+
name|UriToConnectTo
argument_list|)
expr_stmt|;
name|StompConnection
name|connection
init|=
operator|new
name|StompConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|open
argument_list|(
name|UriToConnectTo
operator|.
name|getHost
argument_list|()
argument_list|,
name|UriToConnectTo
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|UriToConnectTo
operator|.
name|getScheme
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"tcp"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"validating alive with connection to: "
operator|+
name|UriToConnectTo
argument_list|)
expr_stmt|;
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|UriToConnectTo
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|(
name|secProps
operator|.
name|getProperty
argument_list|(
literal|"activemq.username"
argument_list|)
argument_list|,
name|secProps
operator|.
name|getProperty
argument_list|(
literal|"activemq.password"
argument_list|)
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
break|break;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"not validating connection to: "
operator|+
name|UriToConnectTo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|broker
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"activemq.base"
argument_list|,
literal|"target"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"activemq.home"
argument_list|,
literal|"target"
argument_list|)
expr_stmt|;
comment|// not a valid home but ok for xml validation
name|System
operator|.
name|setProperty
argument_list|(
literal|"activemq.data"
argument_list|,
literal|"target"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"activemq.conf"
argument_list|,
literal|"target/conf"
argument_list|)
expr_stmt|;
name|secProps
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|secProps
operator|.
name|load
argument_list|(
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/conf/credentials.properties"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

