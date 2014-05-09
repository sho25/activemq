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
name|web
package|;
end_package

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|FrameworkUtil
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

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|web
operator|.
name|context
operator|.
name|WebApplicationContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|web
operator|.
name|context
operator|.
name|support
operator|.
name|WebApplicationContextUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|web
operator|.
name|context
operator|.
name|support
operator|.
name|XmlWebApplicationContext
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
name|servlet
operator|.
name|ServletContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContextEvent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContextListener
import|;
end_import

begin_comment
comment|/**  * Starts the WebConsole.  */
end_comment

begin_class
specifier|public
class|class
name|WebConsoleStarter
implements|implements
name|ServletContextListener
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
name|WebConsoleStarter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|contextInitialized
parameter_list|(
name|ServletContextEvent
name|event
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Initializing ActiveMQ WebConsole..."
argument_list|)
expr_stmt|;
name|String
name|webconsoleType
init|=
name|getWebconsoleType
argument_list|()
decl_stmt|;
name|ServletContext
name|servletContext
init|=
name|event
operator|.
name|getServletContext
argument_list|()
decl_stmt|;
name|WebApplicationContext
name|context
init|=
name|createWebapplicationContext
argument_list|(
name|servletContext
argument_list|,
name|webconsoleType
argument_list|)
decl_stmt|;
name|initializeWebClient
argument_list|(
name|servletContext
argument_list|,
name|context
argument_list|)
expr_stmt|;
comment|// for embedded console log what port it uses
if|if
condition|(
literal|"embedded"
operator|.
name|equals
argument_list|(
name|webconsoleType
argument_list|)
condition|)
block|{
comment|// show the url for the web consoles / main page so people can spot it
name|String
name|port
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"jetty.port"
argument_list|)
decl_stmt|;
name|String
name|host
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"jetty.host"
argument_list|)
decl_stmt|;
if|if
condition|(
name|host
operator|!=
literal|null
operator|&&
name|port
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"ActiveMQ WebConsole available at http://{}:{}/"
argument_list|,
name|host
argument_list|,
name|port
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"ActiveMQ WebConsole initialized."
argument_list|)
expr_stmt|;
block|}
specifier|private
name|WebApplicationContext
name|createWebapplicationContext
parameter_list|(
name|ServletContext
name|servletContext
parameter_list|,
name|String
name|webconsoleType
parameter_list|)
block|{
name|String
name|configuration
init|=
literal|"/WEB-INF/webconsole-"
operator|+
name|webconsoleType
operator|+
literal|".xml"
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Web console type: "
operator|+
name|webconsoleType
argument_list|)
expr_stmt|;
name|XmlWebApplicationContext
name|context
init|=
operator|new
name|XmlWebApplicationContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setServletContext
argument_list|(
name|servletContext
argument_list|)
expr_stmt|;
name|context
operator|.
name|setConfigLocations
argument_list|(
operator|new
name|String
index|[]
block|{
name|configuration
block|}
argument_list|)
expr_stmt|;
name|context
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|context
operator|.
name|start
argument_list|()
expr_stmt|;
name|servletContext
operator|.
name|setAttribute
argument_list|(
name|WebApplicationContext
operator|.
name|ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE
argument_list|,
name|context
argument_list|)
expr_stmt|;
return|return
name|context
return|;
block|}
specifier|private
name|void
name|initializeWebClient
parameter_list|(
name|ServletContext
name|servletContext
parameter_list|,
name|WebApplicationContext
name|context
parameter_list|)
block|{
name|ConnectionFactory
name|connectionFactory
init|=
operator|(
name|ConnectionFactory
operator|)
name|context
operator|.
name|getBean
argument_list|(
literal|"connectionFactory"
argument_list|)
decl_stmt|;
name|servletContext
operator|.
name|setAttribute
argument_list|(
name|WebClient
operator|.
name|CONNECTION_FACTORY_ATTRIBUTE
argument_list|,
name|connectionFactory
argument_list|)
expr_stmt|;
name|WebClient
operator|.
name|initContext
argument_list|(
name|servletContext
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|contextDestroyed
parameter_list|(
name|ServletContextEvent
name|event
parameter_list|)
block|{
name|XmlWebApplicationContext
name|context
init|=
operator|(
name|XmlWebApplicationContext
operator|)
name|WebApplicationContextUtils
operator|.
name|getWebApplicationContext
argument_list|(
name|event
operator|.
name|getServletContext
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|stop
argument_list|()
expr_stmt|;
name|context
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
comment|// do nothing, since the context is destroyed anyway
block|}
specifier|private
specifier|static
name|String
name|getWebconsoleType
parameter_list|()
block|{
name|String
name|webconsoleType
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"webconsole.type"
argument_list|,
literal|"embedded"
argument_list|)
decl_stmt|;
comment|// detect osgi
try|try
block|{
if|if
condition|(
name|OsgiUtil
operator|.
name|isOsgi
argument_list|()
condition|)
block|{
name|webconsoleType
operator|=
literal|"osgi"
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NoClassDefFoundError
name|ignore
parameter_list|)
block|{         }
return|return
name|webconsoleType
return|;
block|}
specifier|static
class|class
name|OsgiUtil
block|{
specifier|static
name|boolean
name|isOsgi
parameter_list|()
block|{
return|return
operator|(
name|FrameworkUtil
operator|.
name|getBundle
argument_list|(
name|WebConsoleStarter
operator|.
name|class
argument_list|)
operator|!=
literal|null
operator|)
return|;
block|}
block|}
block|}
end_class

end_unit

