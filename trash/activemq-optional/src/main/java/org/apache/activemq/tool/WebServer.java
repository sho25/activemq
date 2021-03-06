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
name|tool
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Connector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|bio
operator|.
name|SocketConnector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|webapp
operator|.
name|WebAppContext
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|WebServer
block|{
specifier|public
specifier|static
specifier|final
name|int
name|PORT
init|=
literal|8080
decl_stmt|;
comment|// public static final String WEBAPP_DIR = "target/activemq";
specifier|public
specifier|static
specifier|final
name|String
name|WEBAPP_DIR
init|=
literal|"src/webapp"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|WEBAPP_CTX
init|=
literal|"/"
decl_stmt|;
specifier|private
name|WebServer
parameter_list|()
block|{     }
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Server
name|server
init|=
operator|new
name|Server
argument_list|()
decl_stmt|;
name|Connector
name|context
init|=
operator|new
name|SocketConnector
argument_list|()
decl_stmt|;
name|context
operator|.
name|setServer
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|context
operator|.
name|setPort
argument_list|(
name|PORT
argument_list|)
expr_stmt|;
name|String
name|webappDir
init|=
name|WEBAPP_DIR
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|webappDir
operator|=
name|args
index|[
literal|0
index|]
expr_stmt|;
block|}
name|WebAppContext
name|webapp
init|=
operator|new
name|WebAppContext
argument_list|()
decl_stmt|;
name|webapp
operator|.
name|setServer
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|webapp
operator|.
name|setContextPath
argument_list|(
name|WEBAPP_CTX
argument_list|)
expr_stmt|;
name|webapp
operator|.
name|setResourceBase
argument_list|(
name|webappDir
argument_list|)
expr_stmt|;
name|server
operator|.
name|setHandler
argument_list|(
name|webapp
argument_list|)
expr_stmt|;
name|server
operator|.
name|setConnectors
argument_list|(
operator|new
name|Connector
index|[]
block|{
name|context
block|}
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

