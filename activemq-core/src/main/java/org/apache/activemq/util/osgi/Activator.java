begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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
operator|.
name|osgi
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|ArrayList
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
name|java
operator|.
name|net
operator|.
name|URL
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
name|FactoryFinder
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
name|FactoryFinder
operator|.
name|ObjectFactory
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
name|osgi
operator|.
name|framework
operator|.
name|Bundle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|BundleActivator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|BundleEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|SynchronousBundleListener
import|;
end_import

begin_comment
comment|/**  * An OSGi bundle activator for ActiveMQ which adapts the {@link org.apache.activemq.util.FactoryFinder}  * to the OSGi environment.  *  */
end_comment

begin_class
specifier|public
class|class
name|Activator
implements|implements
name|BundleActivator
implements|,
name|SynchronousBundleListener
implements|,
name|ObjectFactory
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
name|Activator
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|>
name|serviceCache
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|Long
argument_list|,
name|BundleWrapper
argument_list|>
name|bundleWrappers
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Long
argument_list|,
name|BundleWrapper
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|BundleContext
name|bundleContext
decl_stmt|;
comment|/**      * constructor for use as a blueprint bean rather than bundle activator      * @param bundleContext      */
specifier|public
name|Activator
parameter_list|(
name|BundleContext
name|bundleContext
parameter_list|)
throws|throws
name|Exception
block|{
name|start
argument_list|(
name|bundleContext
argument_list|)
expr_stmt|;
block|}
comment|// ================================================================
comment|// BundleActivator interface impl
comment|// ================================================================
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|(
name|BundleContext
name|bundleContext
parameter_list|)
throws|throws
name|Exception
block|{
comment|// This is how we replace the default FactoryFinder strategy
comment|// with one that is more compatible in an OSGi env.
name|FactoryFinder
operator|.
name|setObjectFactory
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|debug
argument_list|(
literal|"activating"
argument_list|)
expr_stmt|;
name|this
operator|.
name|bundleContext
operator|=
name|bundleContext
expr_stmt|;
name|debug
argument_list|(
literal|"checking existing bundles"
argument_list|)
expr_stmt|;
for|for
control|(
name|Bundle
name|bundle
range|:
name|bundleContext
operator|.
name|getBundles
argument_list|()
control|)
block|{
if|if
condition|(
name|bundle
operator|.
name|getState
argument_list|()
operator|==
name|Bundle
operator|.
name|RESOLVED
operator|||
name|bundle
operator|.
name|getState
argument_list|()
operator|==
name|Bundle
operator|.
name|STARTING
operator|||
name|bundle
operator|.
name|getState
argument_list|()
operator|==
name|Bundle
operator|.
name|ACTIVE
operator|||
name|bundle
operator|.
name|getState
argument_list|()
operator|==
name|Bundle
operator|.
name|STOPPING
condition|)
block|{
name|register
argument_list|(
name|bundle
argument_list|)
expr_stmt|;
block|}
block|}
name|debug
argument_list|(
literal|"activated"
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|(
name|BundleContext
name|bundleContext
parameter_list|)
throws|throws
name|Exception
block|{
name|debug
argument_list|(
literal|"deactivating"
argument_list|)
expr_stmt|;
name|bundleContext
operator|.
name|removeBundleListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|bundleWrappers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|unregister
argument_list|(
name|bundleWrappers
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|debug
argument_list|(
literal|"deactivated"
argument_list|)
expr_stmt|;
name|this
operator|.
name|bundleContext
operator|=
literal|null
expr_stmt|;
block|}
comment|// ================================================================
comment|// SynchronousBundleListener interface impl
comment|// ================================================================
specifier|public
name|void
name|bundleChanged
parameter_list|(
name|BundleEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|BundleEvent
operator|.
name|RESOLVED
condition|)
block|{
name|register
argument_list|(
name|event
operator|.
name|getBundle
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|BundleEvent
operator|.
name|UNRESOLVED
operator|||
name|event
operator|.
name|getType
argument_list|()
operator|==
name|BundleEvent
operator|.
name|UNINSTALLED
condition|)
block|{
name|unregister
argument_list|(
name|event
operator|.
name|getBundle
argument_list|()
operator|.
name|getBundleId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|register
parameter_list|(
specifier|final
name|Bundle
name|bundle
parameter_list|)
block|{
name|debug
argument_list|(
literal|"checking bundle "
operator|+
name|bundle
operator|.
name|getBundleId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isImportingUs
argument_list|(
name|bundle
argument_list|)
condition|)
block|{
name|debug
argument_list|(
literal|"The bundle does not import us: "
operator|+
name|bundle
operator|.
name|getBundleId
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|bundleWrappers
operator|.
name|put
argument_list|(
name|bundle
operator|.
name|getBundleId
argument_list|()
argument_list|,
operator|new
name|BundleWrapper
argument_list|(
name|bundle
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * When bundles unload.. we remove them thier cached Class entries from the      * serviceCache.  Future service lookups for the service will fail.      *      * TODO: consider a way to get the Broker release any references to      * instances of the service.      *      * @param bundleId      */
specifier|protected
name|void
name|unregister
parameter_list|(
name|long
name|bundleId
parameter_list|)
block|{
name|BundleWrapper
name|bundle
init|=
name|bundleWrappers
operator|.
name|remove
argument_list|(
name|bundleId
argument_list|)
decl_stmt|;
if|if
condition|(
name|bundle
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|path
range|:
name|bundle
operator|.
name|cachedServices
control|)
block|{
name|debug
argument_list|(
literal|"unregistering service for key: "
operator|+
name|path
argument_list|)
expr_stmt|;
name|serviceCache
operator|.
name|remove
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// ================================================================
comment|// ObjectFactory interface impl
comment|// ================================================================
specifier|public
name|Object
name|create
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IllegalAccessException
throws|,
name|InstantiationException
throws|,
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|Class
name|clazz
init|=
name|serviceCache
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|clazz
operator|==
literal|null
condition|)
block|{
name|StringBuffer
name|warnings
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
comment|// We need to look for a bundle that has that class.
name|int
name|wrrningCounter
init|=
literal|1
decl_stmt|;
for|for
control|(
name|BundleWrapper
name|wrapper
range|:
name|bundleWrappers
operator|.
name|values
argument_list|()
control|)
block|{
name|URL
name|resource
init|=
name|wrapper
operator|.
name|bundle
operator|.
name|getResource
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|Properties
name|properties
init|=
name|loadProperties
argument_list|(
name|resource
argument_list|)
decl_stmt|;
name|String
name|className
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"class"
argument_list|)
decl_stmt|;
if|if
condition|(
name|className
operator|==
literal|null
condition|)
block|{
name|warnings
operator|.
name|append
argument_list|(
literal|"("
operator|+
operator|(
name|wrrningCounter
operator|++
operator|)
operator|+
literal|") Invalid sevice file in bundle "
operator|+
name|wrapper
operator|+
literal|": 'class' property not defined."
argument_list|)
expr_stmt|;
continue|continue;
block|}
try|try
block|{
name|clazz
operator|=
name|wrapper
operator|.
name|bundle
operator|.
name|loadClass
argument_list|(
name|className
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|warnings
operator|.
name|append
argument_list|(
literal|"("
operator|+
operator|(
name|wrrningCounter
operator|++
operator|)
operator|+
literal|") Bundle "
operator|+
name|wrapper
operator|+
literal|" could not load "
operator|+
name|className
operator|+
literal|": "
operator|+
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// Yay.. the class was found.  Now cache it.
name|serviceCache
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|cachedServices
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|clazz
operator|==
literal|null
condition|)
block|{
comment|// Since OSGi is such a tricky enviorment to work in.. lets give folks the
comment|// most information we can in the error message.
name|String
name|msg
init|=
literal|"Service not found: '"
operator|+
name|path
operator|+
literal|"'"
decl_stmt|;
if|if
condition|(
name|warnings
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|msg
operator|+=
literal|", "
operator|+
name|warnings
expr_stmt|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
return|return
name|clazz
operator|.
name|newInstance
argument_list|()
return|;
block|}
comment|// ================================================================
comment|// Internal Helper Methods
comment|// ================================================================
specifier|private
name|void
name|debug
parameter_list|(
name|Object
name|msg
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Properties
name|loadProperties
parameter_list|(
name|URL
name|resource
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|in
init|=
name|resource
operator|.
name|openStream
argument_list|()
decl_stmt|;
try|try
block|{
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|load
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|properties
return|;
block|}
finally|finally
block|{
try|try
block|{
name|in
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
block|{             }
block|}
block|}
specifier|private
name|boolean
name|isImportingUs
parameter_list|(
name|Bundle
name|bundle
parameter_list|)
block|{
try|try
block|{
comment|// If that bundle can load our classes.. then it must be importing us.
return|return
name|bundle
operator|.
name|loadClass
argument_list|(
name|Activator
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|==
name|Activator
operator|.
name|class
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|BundleWrapper
block|{
specifier|private
specifier|final
name|Bundle
name|bundle
decl_stmt|;
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|String
argument_list|>
name|cachedServices
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|BundleWrapper
parameter_list|(
name|Bundle
name|bundle
parameter_list|)
block|{
name|this
operator|.
name|bundle
operator|=
name|bundle
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

