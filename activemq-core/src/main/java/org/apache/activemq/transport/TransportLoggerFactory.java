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
name|jmx
operator|.
name|ManagementContext
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
name|IOExceptionSupport
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
name|LogWriterFinder
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
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_comment
comment|/**  * Singleton class to create TransportLogger objects.  * When the method getInstance() is called for the first time,  * a TransportLoggerControlMBean is created and registered.  * This MBean permits enabling and disabling the logging for  * all TransportLogger objects at once.  *   * @author David Martin Clavo david(dot)martin(dot)clavo(at)gmail.com  * @version $Revision$  * @see TransportLoggerControlMBean  */
end_comment

begin_class
specifier|public
class|class
name|TransportLoggerFactory
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TransportLoggerFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|TransportLoggerFactory
name|instance
decl_stmt|;
specifier|private
specifier|static
name|int
name|lastId
init|=
literal|0
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|LogWriterFinder
name|logWriterFinder
init|=
operator|new
name|LogWriterFinder
argument_list|(
literal|"META-INF/services/org/apache/activemq/transport/logwriters/"
argument_list|)
decl_stmt|;
comment|/**      * LogWriter that will be used if none is specified.      */
specifier|public
specifier|static
name|String
name|defaultLogWriterName
init|=
literal|"default"
decl_stmt|;
comment|/**      * If transport logging is enabled, it will be possible to control      * the transport loggers or not based on this value       */
specifier|private
specifier|static
name|boolean
name|defaultDynamicManagement
init|=
literal|false
decl_stmt|;
comment|/**      * If transport logging is enabled, the transport loggers will initially      * output or not depending on this value.      * This setting only has a meaning if       */
specifier|private
specifier|static
name|boolean
name|defaultInitialBehavior
init|=
literal|true
decl_stmt|;
comment|/**      * Default port to control the transport loggers through JMX      */
specifier|private
specifier|static
name|int
name|defaultJmxPort
init|=
literal|1099
decl_stmt|;
specifier|private
name|boolean
name|transportLoggerControlCreated
init|=
literal|false
decl_stmt|;
specifier|private
name|ManagementContext
name|managementContext
decl_stmt|;
specifier|private
name|ObjectName
name|objectName
decl_stmt|;
comment|/**      * Private constructor.      */
specifier|private
name|TransportLoggerFactory
parameter_list|()
block|{     }
comment|/**      * Returns a TransportLoggerFactory object which can be used to create TransportLogger objects.      * @return a TransportLoggerFactory object      */
specifier|public
specifier|static
specifier|synchronized
name|TransportLoggerFactory
name|getInstance
parameter_list|()
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|instance
operator|=
operator|new
name|TransportLoggerFactory
argument_list|()
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|this
operator|.
name|transportLoggerControlCreated
condition|)
block|{
name|this
operator|.
name|managementContext
operator|.
name|unregisterMBean
argument_list|(
name|this
operator|.
name|objectName
argument_list|)
expr_stmt|;
name|this
operator|.
name|managementContext
operator|.
name|stop
argument_list|()
expr_stmt|;
name|this
operator|.
name|managementContext
operator|=
literal|null
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
name|error
argument_list|(
literal|"TransportLoggerFactory could not be stopped, reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Creates a TransportLogger object, that will be inserted in the Transport Stack.      * Uses the default initial behavior, the default log writer, and creates a new      * log4j object to be used by the TransportLogger.      * @param next The next Transport layer in the Transport stack.      * @return A TransportLogger object.      * @throws IOException      */
specifier|public
name|TransportLogger
name|createTransportLogger
parameter_list|(
name|Transport
name|next
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|id
init|=
name|getNextId
argument_list|()
decl_stmt|;
return|return
name|createTransportLogger
argument_list|(
name|next
argument_list|,
name|id
argument_list|,
name|createLog
argument_list|(
name|id
argument_list|)
argument_list|,
name|defaultLogWriterName
argument_list|,
name|defaultDynamicManagement
argument_list|,
name|defaultInitialBehavior
argument_list|,
name|defaultJmxPort
argument_list|)
return|;
block|}
comment|/**      * Creates a TransportLogger object, that will be inserted in the Transport Stack.      * Uses the default initial behavior and the default log writer.      * @param next The next Transport layer in the Transport stack.      * @param log The log4j log that will be used by the TransportLogger.      * @return A TransportLogger object.      * @throws IOException      */
specifier|public
name|TransportLogger
name|createTransportLogger
parameter_list|(
name|Transport
name|next
parameter_list|,
name|Log
name|log
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createTransportLogger
argument_list|(
name|next
argument_list|,
name|getNextId
argument_list|()
argument_list|,
name|log
argument_list|,
name|defaultLogWriterName
argument_list|,
name|defaultDynamicManagement
argument_list|,
name|defaultInitialBehavior
argument_list|,
name|defaultJmxPort
argument_list|)
return|;
block|}
comment|/**      * Creates a TransportLogger object, that will be inserted in the Transport Stack.      * Creates a new log4j object to be used by the TransportLogger.      * @param next The next Transport layer in the Transport stack.      * @param startLogging Specifies if this TransportLogger should be initially active or not.      * @param logWriterName The name or the LogWriter to be used. Different log writers can output      * logs with a different format.      * @return A TransportLogger object.      * @throws IOException      */
specifier|public
name|TransportLogger
name|createTransportLogger
parameter_list|(
name|Transport
name|next
parameter_list|,
name|String
name|logWriterName
parameter_list|,
name|boolean
name|useJmx
parameter_list|,
name|boolean
name|startLogging
parameter_list|,
name|int
name|jmxport
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|id
init|=
name|getNextId
argument_list|()
decl_stmt|;
return|return
name|createTransportLogger
argument_list|(
name|next
argument_list|,
name|id
argument_list|,
name|createLog
argument_list|(
name|id
argument_list|)
argument_list|,
name|logWriterName
argument_list|,
name|useJmx
argument_list|,
name|startLogging
argument_list|,
name|jmxport
argument_list|)
return|;
block|}
comment|/**      * Creates a TransportLogger object, that will be inserted in the Transport Stack.      * @param next The next Transport layer in the Transport stack.      * @param id The id of the transport logger.      * @param log The log4j log that will be used by the TransportLogger.      * @param logWriterName The name or the LogWriter to be used. Different log writers can output      * @param dynamicManagement Specifies if JMX will be used to switch on/off the TransportLogger to be created.      * @param startLogging Specifies if this TransportLogger should be initially active or not. Only has a meaning if      * dynamicManagement = true.      * @param jmxPort the port to be used by the JMX server. It should only be different from 1099 (broker's default JMX port)      * when it's a client that is using Transport Logging. In a broker, if the port is different from 1099, 2 JMX servers will      * be created, both identical, with all the MBeans.      * @return A TransportLogger object.      * @throws IOException      */
specifier|public
name|TransportLogger
name|createTransportLogger
parameter_list|(
name|Transport
name|next
parameter_list|,
name|int
name|id
parameter_list|,
name|Log
name|log
parameter_list|,
name|String
name|logWriterName
parameter_list|,
name|boolean
name|dynamicManagement
parameter_list|,
name|boolean
name|startLogging
parameter_list|,
name|int
name|jmxport
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|LogWriter
name|logWriter
init|=
name|logWriterFinder
operator|.
name|newInstance
argument_list|(
name|logWriterName
argument_list|)
decl_stmt|;
name|TransportLogger
name|tl
init|=
operator|new
name|TransportLogger
argument_list|(
name|next
argument_list|,
name|log
argument_list|,
name|startLogging
argument_list|,
name|logWriter
argument_list|)
decl_stmt|;
if|if
condition|(
name|dynamicManagement
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|transportLoggerControlCreated
condition|)
block|{
name|this
operator|.
name|createTransportLoggerControl
argument_list|(
name|jmxport
argument_list|)
expr_stmt|;
block|}
block|}
name|TransportLoggerView
name|tlv
init|=
operator|new
name|TransportLoggerView
argument_list|(
name|tl
argument_list|,
name|next
operator|.
name|toString
argument_list|()
argument_list|,
name|id
argument_list|,
name|this
operator|.
name|managementContext
argument_list|)
decl_stmt|;
name|tl
operator|.
name|setView
argument_list|(
name|tlv
argument_list|)
expr_stmt|;
block|}
return|return
name|tl
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Could not create log writer object for: "
operator|+
name|logWriterName
operator|+
literal|", reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|synchronized
specifier|private
specifier|static
name|int
name|getNextId
parameter_list|()
block|{
return|return
operator|++
name|lastId
return|;
block|}
specifier|private
specifier|static
name|Log
name|createLog
parameter_list|(
name|int
name|id
parameter_list|)
block|{
return|return
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TransportLogger
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".Connection:"
operator|+
name|id
argument_list|)
return|;
block|}
comment|/**      * Starts the management context.      * Creates and registers a TransportLoggerControl MBean which enables the user      * to enable/disable logging for all transport loggers at once.      */
specifier|private
name|void
name|createTransportLoggerControl
parameter_list|(
name|int
name|port
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|managementContext
operator|=
operator|new
name|ManagementContext
argument_list|()
expr_stmt|;
name|this
operator|.
name|managementContext
operator|.
name|setConnectorPort
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|this
operator|.
name|managementContext
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Management context could not be started, reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|this
operator|.
name|objectName
operator|=
operator|new
name|ObjectName
argument_list|(
name|this
operator|.
name|managementContext
operator|.
name|getJmxDomainName
argument_list|()
operator|+
literal|":"
operator|+
literal|"Type=TransportLoggerControl"
argument_list|)
expr_stmt|;
name|this
operator|.
name|managementContext
operator|.
name|registerMBean
argument_list|(
operator|new
name|TransportLoggerControl
argument_list|(
name|this
operator|.
name|managementContext
argument_list|)
argument_list|,
name|this
operator|.
name|objectName
argument_list|)
expr_stmt|;
name|this
operator|.
name|transportLoggerControlCreated
operator|=
literal|true
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
name|error
argument_list|(
literal|"TransportLoggerControlMBean could not be registered, reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

