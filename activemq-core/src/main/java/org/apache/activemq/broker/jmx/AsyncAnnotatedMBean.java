begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|util
operator|.
name|concurrent
operator|.
name|Callable
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
name|ExecutionException
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
name|ExecutorService
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
name|Future
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
name|javax
operator|.
name|management
operator|.
name|MBeanException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|NotCompliantMBeanException
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
name|ReflectionException
import|;
end_import

begin_comment
comment|/**  * MBean that invokes the requested operation using an async operation and waits for the result  * if the operation times out then an exception is thrown.  */
end_comment

begin_class
specifier|public
class|class
name|AsyncAnnotatedMBean
extends|extends
name|AnnotatedMBean
block|{
specifier|private
name|ExecutorService
name|executor
decl_stmt|;
specifier|private
name|long
name|timeout
init|=
literal|0
decl_stmt|;
specifier|public
parameter_list|<
name|T
parameter_list|>
name|AsyncAnnotatedMBean
parameter_list|(
name|ExecutorService
name|executor
parameter_list|,
name|long
name|timeout
parameter_list|,
name|T
name|impl
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|mbeanInterface
parameter_list|)
throws|throws
name|NotCompliantMBeanException
block|{
name|super
argument_list|(
name|impl
argument_list|,
name|mbeanInterface
argument_list|)
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
block|}
specifier|protected
name|AsyncAnnotatedMBean
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|mbeanInterface
parameter_list|)
throws|throws
name|NotCompliantMBeanException
block|{
name|super
argument_list|(
name|mbeanInterface
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Object
name|asyncInvole
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
index|[]
name|objects
parameter_list|,
name|String
index|[]
name|strings
parameter_list|)
throws|throws
name|MBeanException
throws|,
name|ReflectionException
block|{
return|return
name|super
operator|.
name|invoke
argument_list|(
name|s
argument_list|,
name|objects
argument_list|,
name|strings
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
specifier|public
specifier|static
name|void
name|registerMBean
parameter_list|(
name|ExecutorService
name|executor
parameter_list|,
name|long
name|timeout
parameter_list|,
name|ManagementContext
name|context
parameter_list|,
name|Object
name|object
parameter_list|,
name|ObjectName
name|objectName
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|timeout
operator|<
literal|0
operator|&&
name|executor
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"async timeout cannot be negative."
argument_list|)
throw|;
block|}
if|if
condition|(
name|timeout
operator|>
literal|0
operator|&&
name|executor
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"timeout given but no ExecutorService instance given."
argument_list|)
throw|;
block|}
name|String
name|mbeanName
init|=
name|object
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"MBean"
decl_stmt|;
for|for
control|(
name|Class
name|c
range|:
name|object
operator|.
name|getClass
argument_list|()
operator|.
name|getInterfaces
argument_list|()
control|)
block|{
if|if
condition|(
name|mbeanName
operator|.
name|equals
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|timeout
operator|==
literal|0
condition|)
block|{
name|context
operator|.
name|registerMBean
argument_list|(
operator|new
name|AnnotatedMBean
argument_list|(
name|object
argument_list|,
name|c
argument_list|)
argument_list|,
name|objectName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|context
operator|.
name|registerMBean
argument_list|(
operator|new
name|AsyncAnnotatedMBean
argument_list|(
name|executor
argument_list|,
name|timeout
argument_list|,
name|object
argument_list|,
name|c
argument_list|)
argument_list|,
name|objectName
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
block|}
name|context
operator|.
name|registerMBean
argument_list|(
name|object
argument_list|,
name|objectName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|invoke
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
index|[]
name|objects
parameter_list|,
name|String
index|[]
name|strings
parameter_list|)
throws|throws
name|MBeanException
throws|,
name|ReflectionException
block|{
specifier|final
name|String
name|action
init|=
name|s
decl_stmt|;
specifier|final
name|Object
index|[]
name|params
init|=
name|objects
decl_stmt|;
specifier|final
name|String
index|[]
name|signature
init|=
name|strings
decl_stmt|;
name|Future
argument_list|<
name|Object
argument_list|>
name|task
init|=
name|executor
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|asyncInvole
argument_list|(
name|action
argument_list|,
name|params
argument_list|,
name|signature
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|task
operator|.
name|get
argument_list|(
name|timeout
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|MBeanException
condition|)
block|{
throw|throw
operator|(
name|MBeanException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
throw|throw
operator|new
name|MBeanException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MBeanException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|task
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|task
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

