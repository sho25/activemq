begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|WeakReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|JMXSupport
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
comment|/**  * Class implementing the TransportLoggerViewMBean interface.  * When an object of this class is created, it registers itself in  * the MBeanServer of the management context provided.  * When a TransportLogger object is finalized because the Transport Stack  * where it resides is no longer in use, the method unregister() will be called.   * @see TransportLoggerViewMBean.  */
end_comment

begin_class
specifier|public
class|class
name|TransportLoggerView
implements|implements
name|TransportLoggerViewMBean
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TransportLoggerView
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Set with the TransportLoggerViews objects created.      * Used by the methods enableAllTransportLoggers and diablellTransportLoggers.      * The method unregister() removes objects from this set.      */
specifier|private
specifier|static
name|Set
argument_list|<
name|TransportLoggerView
argument_list|>
name|transportLoggerViews
init|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|TransportLoggerView
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|WeakReference
argument_list|<
name|TransportLogger
argument_list|>
name|transportLogger
decl_stmt|;
specifier|private
specifier|final
name|String
name|nextTransportName
decl_stmt|;
specifier|private
specifier|final
name|int
name|id
decl_stmt|;
specifier|private
specifier|final
name|ManagementContext
name|managementContext
decl_stmt|;
specifier|private
specifier|final
name|ObjectName
name|name
decl_stmt|;
comment|/**      * Constructor.      * @param transportLogger The TransportLogger object which is to be managed by this MBean.      * @param nextTransportName The name of the next TransportLayer. This is used to give a unique      * name for each MBean of the TransportLoggerView class.      * @param id The id of the TransportLogger to be watched.      * @param managementContext The management context who has the MBeanServer where this MBean will be registered.      */
specifier|public
name|TransportLoggerView
parameter_list|(
name|TransportLogger
name|transportLogger
parameter_list|,
name|String
name|nextTransportName
parameter_list|,
name|int
name|id
parameter_list|,
name|ManagementContext
name|managementContext
parameter_list|)
block|{
name|this
operator|.
name|transportLogger
operator|=
operator|new
name|WeakReference
argument_list|<
name|TransportLogger
argument_list|>
argument_list|(
name|transportLogger
argument_list|)
expr_stmt|;
name|this
operator|.
name|nextTransportName
operator|=
name|nextTransportName
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|managementContext
operator|=
name|managementContext
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|this
operator|.
name|createTransportLoggerObjectName
argument_list|()
expr_stmt|;
name|TransportLoggerView
operator|.
name|transportLoggerViews
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|register
argument_list|()
expr_stmt|;
block|}
comment|/**      * Enable logging for all Transport Loggers at once.      */
specifier|public
specifier|static
name|void
name|enableAllTransportLoggers
parameter_list|()
block|{
for|for
control|(
name|TransportLoggerView
name|view
range|:
name|transportLoggerViews
control|)
block|{
name|view
operator|.
name|enableLogging
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Disable logging for all Transport Loggers at once.      */
specifier|public
specifier|static
name|void
name|disableAllTransportLoggers
parameter_list|()
block|{
for|for
control|(
name|TransportLoggerView
name|view
range|:
name|transportLoggerViews
control|)
block|{
name|view
operator|.
name|disableLogging
argument_list|()
expr_stmt|;
block|}
block|}
comment|// doc comment inherited from TransportLoggerViewMBean
specifier|public
name|void
name|enableLogging
parameter_list|()
block|{
name|this
operator|.
name|setLogging
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// doc comment inherited from TransportLoggerViewMBean
specifier|public
name|void
name|disableLogging
parameter_list|()
block|{
name|this
operator|.
name|setLogging
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// doc comment inherited from TransportLoggerViewMBean
specifier|public
name|boolean
name|isLogging
parameter_list|()
block|{
return|return
name|transportLogger
operator|.
name|get
argument_list|()
operator|.
name|isLogging
argument_list|()
return|;
block|}
comment|// doc comment inherited from TransportLoggerViewMBean
specifier|public
name|void
name|setLogging
parameter_list|(
name|boolean
name|logging
parameter_list|)
block|{
name|transportLogger
operator|.
name|get
argument_list|()
operator|.
name|setLogging
argument_list|(
name|logging
argument_list|)
expr_stmt|;
block|}
comment|/**      * Registers this MBean in the MBeanServer of the management context      * provided at creation time. This method is only called by the constructor.      */
specifier|private
name|void
name|register
parameter_list|()
block|{
try|try
block|{
name|this
operator|.
name|managementContext
operator|.
name|getMBeanServer
argument_list|()
operator|.
name|registerMBean
argument_list|(
name|this
argument_list|,
name|this
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not register MBean for TransportLoggerView "
operator|+
name|id
operator|+
literal|"with name "
operator|+
name|this
operator|.
name|name
operator|.
name|toString
argument_list|()
operator|+
literal|", reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Unregisters the MBean from the MBeanServer of the management context      * provided at creation time.      * This method is called by the TransportLogger object being managed when      * the TransportLogger object is finalized, to avoid the memory leak that      * would be caused if MBeans were not unregistered.       */
specifier|public
name|void
name|unregister
parameter_list|()
block|{
name|TransportLoggerView
operator|.
name|transportLoggerViews
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|managementContext
operator|.
name|getMBeanServer
argument_list|()
operator|.
name|unregisterMBean
argument_list|(
name|this
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not unregister MBean for TransportLoggerView "
operator|+
name|id
operator|+
literal|"with name "
operator|+
name|this
operator|.
name|name
operator|.
name|toString
argument_list|()
operator|+
literal|", reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Creates the ObjectName to be used when registering the MBean.      * @return the ObjectName to be used when registering the MBean.      */
specifier|private
name|ObjectName
name|createTransportLoggerObjectName
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|ObjectName
argument_list|(
name|createTransportLoggerObjectNameRoot
argument_list|(
name|this
operator|.
name|managementContext
argument_list|)
operator|+
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
name|TransportLogger
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" "
operator|+
name|this
operator|.
name|id
operator|+
literal|";"
operator|+
name|this
operator|.
name|nextTransportName
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not create ObjectName for TransportLoggerView "
operator|+
name|id
operator|+
literal|", reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Creates the part of the ObjectName that will be used by all MBeans.      * This method is public so it can be used by the TransportLoggerControl class.      * @param managementContext      * @return A String with the part of the ObjectName common to all the TransportLoggerView MBeans.      */
specifier|public
specifier|static
name|String
name|createTransportLoggerObjectNameRoot
parameter_list|(
name|ManagementContext
name|managementContext
parameter_list|)
block|{
return|return
name|managementContext
operator|.
name|getJmxDomainName
argument_list|()
operator|+
literal|":"
operator|+
literal|"Type=TransportLogger,"
operator|+
literal|"TransportLoggerName="
return|;
block|}
block|}
end_class

end_unit

