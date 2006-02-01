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
name|broker
operator|.
name|jmx
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
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|registry
operator|.
name|LocateRegistry
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
name|javax
operator|.
name|management
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|JMException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MalformedObjectNameException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXConnectorServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXConnectorServerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXServiceURL
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
name|Service
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
comment|/**  * A Flow provides different dispatch policies within the NMR  *   * @org.xbean.XBean  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ManagementContext
implements|implements
name|Service
block|{
comment|/**      * Default activemq domain      */
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_DOMAIN
init|=
literal|"org.apache.activemq"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ManagementContext
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|MBeanServer
name|beanServer
decl_stmt|;
specifier|private
name|String
name|jmxDomainName
init|=
name|DEFAULT_DOMAIN
decl_stmt|;
specifier|private
name|boolean
name|useMBeanServer
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|createMBeanServer
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|locallyCreateMBeanServer
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|createConnector
init|=
literal|true
decl_stmt|;
specifier|private
name|int
name|connectorPort
init|=
literal|1099
decl_stmt|;
specifier|private
name|String
name|connectorPath
init|=
literal|"/jmxrmi"
decl_stmt|;
specifier|private
name|JMXConnectorServer
name|connectorServer
decl_stmt|;
specifier|private
name|ObjectName
name|namingServiceObjectName
decl_stmt|;
specifier|public
name|ManagementContext
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ManagementContext
parameter_list|(
name|MBeanServer
name|server
parameter_list|)
block|{
name|this
operator|.
name|beanServer
operator|=
name|server
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
comment|// lets force the MBeanServer to be created if needed
name|getMBeanServer
argument_list|()
expr_stmt|;
if|if
condition|(
name|connectorServer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|getMBeanServer
argument_list|()
operator|.
name|invoke
argument_list|(
name|namingServiceObjectName
argument_list|,
literal|"start"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{             }
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
literal|"JMX connector"
argument_list|)
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|connectorServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"JMX consoles can connect to "
operator|+
name|connectorServer
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to start jmx connector: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|connectorServer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connectorServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to stop jmx connector: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|connectorServer
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|getMBeanServer
argument_list|()
operator|.
name|invoke
argument_list|(
name|namingServiceObjectName
argument_list|,
literal|"stop"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{             }
block|}
if|if
condition|(
name|locallyCreateMBeanServer
operator|&&
name|beanServer
operator|!=
literal|null
condition|)
block|{
comment|// check to see if the factory knows about this server
name|List
name|list
init|=
name|MBeanServerFactory
operator|.
name|findMBeanServer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
operator|&&
operator|!
name|list
operator|.
name|isEmpty
argument_list|()
operator|&&
name|list
operator|.
name|contains
argument_list|(
name|beanServer
argument_list|)
condition|)
block|{
name|MBeanServerFactory
operator|.
name|releaseMBeanServer
argument_list|(
name|beanServer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @return Returns the jmxDomainName.      */
specifier|public
name|String
name|getJmxDomainName
parameter_list|()
block|{
return|return
name|jmxDomainName
return|;
block|}
comment|/**      * @param jmxDomainName      *            The jmxDomainName to set.      */
specifier|public
name|void
name|setJmxDomainName
parameter_list|(
name|String
name|jmxDomainName
parameter_list|)
block|{
name|this
operator|.
name|jmxDomainName
operator|=
name|jmxDomainName
expr_stmt|;
block|}
comment|/**      * Get the MBeanServer      *       * @return the MBeanServer      */
specifier|public
name|MBeanServer
name|getMBeanServer
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|beanServer
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|beanServer
operator|=
name|findMBeanServer
argument_list|()
expr_stmt|;
block|}
return|return
name|beanServer
return|;
block|}
comment|/**      * Set the MBeanServer      * @param beanServer      */
specifier|public
name|void
name|setMBeanServer
parameter_list|(
name|MBeanServer
name|beanServer
parameter_list|)
block|{
name|this
operator|.
name|beanServer
operator|=
name|beanServer
expr_stmt|;
block|}
comment|/**      * @return Returns the useMBeanServer.      */
specifier|public
name|boolean
name|isUseMBeanServer
parameter_list|()
block|{
return|return
name|useMBeanServer
return|;
block|}
comment|/**      * @param useMBeanServer      *            The useMBeanServer to set.      */
specifier|public
name|void
name|setUseMBeanServer
parameter_list|(
name|boolean
name|useMBeanServer
parameter_list|)
block|{
name|this
operator|.
name|useMBeanServer
operator|=
name|useMBeanServer
expr_stmt|;
block|}
comment|/**      * @return Returns the createMBeanServer flag.      */
specifier|public
name|boolean
name|isCreateMBeanServer
parameter_list|()
block|{
return|return
name|createMBeanServer
return|;
block|}
comment|/**      * @param enableJMX      *            Set createMBeanServer.      */
specifier|public
name|void
name|setCreateMBeanServer
parameter_list|(
name|boolean
name|enableJMX
parameter_list|)
block|{
name|this
operator|.
name|createMBeanServer
operator|=
name|enableJMX
expr_stmt|;
block|}
comment|/**      * Formulate and return the MBean ObjectName of a custom control MBean      *       * @param type      * @param name      * @return the JMX ObjectName of the MBean, or<code>null</code> if      *<code>customName</code> is invalid.      */
specifier|public
name|ObjectName
name|createCustomComponentMBeanName
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|ObjectName
name|result
init|=
literal|null
decl_stmt|;
name|String
name|tmp
init|=
name|jmxDomainName
operator|+
literal|":"
operator|+
literal|"type="
operator|+
name|sanitizeString
argument_list|(
name|type
argument_list|)
operator|+
literal|",name="
operator|+
name|sanitizeString
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|result
operator|=
operator|new
name|ObjectName
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedObjectNameException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Couldn't create ObjectName from: "
operator|+
name|type
operator|+
literal|" , "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * The ':' and '/' characters are reserved in ObjectNames      *       * @param in      * @return sanitized String      */
specifier|private
specifier|static
name|String
name|sanitizeString
parameter_list|(
name|String
name|in
parameter_list|)
block|{
name|String
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|in
operator|.
name|replace
argument_list|(
literal|':'
argument_list|,
literal|'_'
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replace
argument_list|(
literal|'/'
argument_list|,
literal|'_'
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
literal|'_'
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * Retrive an System ObjectName      *       * @param domainName      * @param containerName      * @param theClass      * @return the ObjectName      * @throws MalformedObjectNameException      */
specifier|public
specifier|static
name|ObjectName
name|getSystemObjectName
parameter_list|(
name|String
name|domainName
parameter_list|,
name|String
name|containerName
parameter_list|,
name|Class
name|theClass
parameter_list|)
throws|throws
name|MalformedObjectNameException
throws|,
name|NullPointerException
block|{
name|String
name|tmp
init|=
name|domainName
operator|+
literal|":"
operator|+
literal|"type="
operator|+
name|theClass
operator|.
name|getName
argument_list|()
operator|+
literal|",name="
operator|+
name|getRelativeName
argument_list|(
name|containerName
argument_list|,
name|theClass
argument_list|)
decl_stmt|;
return|return
operator|new
name|ObjectName
argument_list|(
name|tmp
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|getRelativeName
parameter_list|(
name|String
name|containerName
parameter_list|,
name|Class
name|theClass
parameter_list|)
block|{
name|String
name|name
init|=
name|theClass
operator|.
name|getName
argument_list|()
decl_stmt|;
name|int
name|index
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|>=
literal|0
operator|&&
operator|(
name|index
operator|+
literal|1
operator|)
operator|<
name|name
operator|.
name|length
argument_list|()
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|containerName
operator|+
literal|"."
operator|+
name|name
return|;
block|}
comment|/**      * Unregister an MBean      *       * @param name      * @throws JMException      */
specifier|public
name|void
name|unregisterMBean
parameter_list|(
name|ObjectName
name|name
parameter_list|)
throws|throws
name|JMException
block|{
if|if
condition|(
name|beanServer
operator|!=
literal|null
operator|&&
name|beanServer
operator|.
name|isRegistered
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|beanServer
operator|.
name|unregisterMBean
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
specifier|synchronized
name|MBeanServer
name|findMBeanServer
parameter_list|()
block|{
name|MBeanServer
name|result
init|=
literal|null
decl_stmt|;
comment|// create the mbean server
try|try
block|{
if|if
condition|(
name|useMBeanServer
condition|)
block|{
comment|// lets piggy back on another MBeanServer -
comment|// we could be in an appserver!
name|List
name|list
init|=
name|MBeanServerFactory
operator|.
name|findMBeanServer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
operator|&&
name|list
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|result
operator|=
operator|(
name|MBeanServer
operator|)
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|result
operator|==
literal|null
operator|&&
name|createMBeanServer
condition|)
block|{
name|result
operator|=
name|createMBeanServer
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NoClassDefFoundError
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Couldnot load MBeanServer"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// probably don't have access to system properties
name|log
operator|.
name|error
argument_list|(
literal|"Failed to initialize MBeanServer"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * @return      * @throws NullPointerException       * @throws MalformedObjectNameException       * @throws IOException       */
specifier|protected
name|MBeanServer
name|createMBeanServer
parameter_list|()
throws|throws
name|MalformedObjectNameException
throws|,
name|IOException
block|{
name|MBeanServer
name|mbeanServer
init|=
name|MBeanServerFactory
operator|.
name|createMBeanServer
argument_list|(
name|jmxDomainName
argument_list|)
decl_stmt|;
name|locallyCreateMBeanServer
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|createConnector
condition|)
block|{
name|createConnector
argument_list|(
name|mbeanServer
argument_list|)
expr_stmt|;
block|}
return|return
name|mbeanServer
return|;
block|}
comment|/**      * @param mbeanServer      * @throws MalformedObjectNameException      * @throws MalformedURLException      * @throws IOException      */
specifier|private
name|void
name|createConnector
parameter_list|(
name|MBeanServer
name|mbeanServer
parameter_list|)
throws|throws
name|MalformedObjectNameException
throws|,
name|MalformedURLException
throws|,
name|IOException
block|{
comment|// Create the NamingService, needed by JSR 160
try|try
block|{
name|LocateRegistry
operator|.
name|createRegistry
argument_list|(
name|connectorPort
argument_list|)
expr_stmt|;
name|namingServiceObjectName
operator|=
name|ObjectName
operator|.
name|getInstance
argument_list|(
literal|"naming:type=rmiregistry"
argument_list|)
expr_stmt|;
comment|//          Do not use the createMBean as the mx4j jar may not be in the
comment|// same class loader than the server
name|Class
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"mx4j.tools.naming.NamingService"
argument_list|)
decl_stmt|;
name|mbeanServer
operator|.
name|registerMBean
argument_list|(
name|cl
operator|.
name|newInstance
argument_list|()
argument_list|,
name|namingServiceObjectName
argument_list|)
expr_stmt|;
comment|//mbeanServer.createMBean("mx4j.tools.naming.NamingService", namingServiceObjectName, null);
comment|// set the naming port
name|Attribute
name|attr
init|=
operator|new
name|Attribute
argument_list|(
literal|"Port"
argument_list|,
operator|new
name|Integer
argument_list|(
name|connectorPort
argument_list|)
argument_list|)
decl_stmt|;
name|mbeanServer
operator|.
name|setAttribute
argument_list|(
name|namingServiceObjectName
argument_list|,
name|attr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Failed to create local registry"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// Create the JMXConnectorServer
name|String
name|serviceURL
init|=
literal|"service:jmx:rmi:///jndi/rmi://localhost:"
operator|+
name|connectorPort
operator|+
name|connectorPath
decl_stmt|;
name|JMXServiceURL
name|url
init|=
operator|new
name|JMXServiceURL
argument_list|(
name|serviceURL
argument_list|)
decl_stmt|;
name|connectorServer
operator|=
name|JMXConnectorServerFactory
operator|.
name|newJMXConnectorServer
argument_list|(
name|url
argument_list|,
literal|null
argument_list|,
name|mbeanServer
argument_list|)
expr_stmt|;
comment|//log.info("JMX consoles can connect to serviceURL: " + serviceURL);
block|}
specifier|public
name|String
name|getConnectorPath
parameter_list|()
block|{
return|return
name|connectorPath
return|;
block|}
specifier|public
name|void
name|setConnectorPath
parameter_list|(
name|String
name|connectorPath
parameter_list|)
block|{
name|this
operator|.
name|connectorPath
operator|=
name|connectorPath
expr_stmt|;
block|}
specifier|public
name|int
name|getConnectorPort
parameter_list|()
block|{
return|return
name|connectorPort
return|;
block|}
specifier|public
name|void
name|setConnectorPort
parameter_list|(
name|int
name|connectorPort
parameter_list|)
block|{
name|this
operator|.
name|connectorPort
operator|=
name|connectorPort
expr_stmt|;
block|}
specifier|public
name|boolean
name|isCreateConnector
parameter_list|()
block|{
return|return
name|createConnector
return|;
block|}
specifier|public
name|void
name|setCreateConnector
parameter_list|(
name|boolean
name|createConnector
parameter_list|)
block|{
name|this
operator|.
name|createConnector
operator|=
name|createConnector
expr_stmt|;
block|}
block|}
end_class

end_unit

