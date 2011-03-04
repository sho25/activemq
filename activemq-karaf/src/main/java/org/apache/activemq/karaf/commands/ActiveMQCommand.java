begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|karaf
operator|.
name|commands
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|gogo
operator|.
name|commands
operator|.
name|Action
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|gogo
operator|.
name|commands
operator|.
name|Argument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|gogo
operator|.
name|commands
operator|.
name|basic
operator|.
name|AbstractCommand
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|gogo
operator|.
name|commands
operator|.
name|basic
operator|.
name|ActionPreparator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|gogo
operator|.
name|commands
operator|.
name|basic
operator|.
name|DefaultActionPreparator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|service
operator|.
name|command
operator|.
name|CommandSession
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|karaf
operator|.
name|shell
operator|.
name|console
operator|.
name|BlueprintContainerAware
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|karaf
operator|.
name|shell
operator|.
name|console
operator|.
name|BundleContextAware
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|karaf
operator|.
name|shell
operator|.
name|console
operator|.
name|CompletableFunction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|karaf
operator|.
name|shell
operator|.
name|console
operator|.
name|Completer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|karaf
operator|.
name|shell
operator|.
name|console
operator|.
name|commands
operator|.
name|GenericType
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
name|service
operator|.
name|blueprint
operator|.
name|container
operator|.
name|BlueprintContainer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|blueprint
operator|.
name|container
operator|.
name|Converter
import|;
end_import

begin_comment
comment|/**  * Base command to process options and wrap native ActiveMQ console commands.  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQCommand
extends|extends
name|AbstractCommand
implements|implements
name|CompletableFunction
block|{
specifier|protected
name|BlueprintContainer
name|blueprintContainer
decl_stmt|;
specifier|protected
name|Converter
name|blueprintConverter
decl_stmt|;
specifier|protected
name|String
name|actionId
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|Completer
argument_list|>
name|completers
decl_stmt|;
specifier|public
name|void
name|setBlueprintContainer
parameter_list|(
name|BlueprintContainer
name|blueprintContainer
parameter_list|)
block|{
name|this
operator|.
name|blueprintContainer
operator|=
name|blueprintContainer
expr_stmt|;
block|}
specifier|public
name|void
name|setBlueprintConverter
parameter_list|(
name|Converter
name|blueprintConverter
parameter_list|)
block|{
name|this
operator|.
name|blueprintConverter
operator|=
name|blueprintConverter
expr_stmt|;
block|}
specifier|public
name|void
name|setActionId
parameter_list|(
name|String
name|actionId
parameter_list|)
block|{
name|this
operator|.
name|actionId
operator|=
name|actionId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Completer
argument_list|>
name|getCompleters
parameter_list|()
block|{
return|return
name|completers
return|;
block|}
specifier|public
name|void
name|setCompleters
parameter_list|(
name|List
argument_list|<
name|Completer
argument_list|>
name|completers
parameter_list|)
block|{
name|this
operator|.
name|completers
operator|=
name|completers
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ActionPreparator
name|getPreparator
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQActionPreparator
argument_list|()
return|;
block|}
class|class
name|ActiveMQActionPreparator
extends|extends
name|DefaultActionPreparator
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|prepare
parameter_list|(
name|Action
name|action
parameter_list|,
name|CommandSession
name|session
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|params
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|Argument
argument_list|,
name|Field
argument_list|>
name|arguments
init|=
operator|new
name|HashMap
argument_list|<
name|Argument
argument_list|,
name|Field
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Argument
argument_list|>
name|orderedArguments
init|=
operator|new
name|ArrayList
argument_list|<
name|Argument
argument_list|>
argument_list|()
decl_stmt|;
comment|// Introspect
for|for
control|(
name|Class
name|type
init|=
name|action
operator|.
name|getClass
argument_list|()
init|;
name|type
operator|!=
literal|null
condition|;
name|type
operator|=
name|type
operator|.
name|getSuperclass
argument_list|()
control|)
block|{
for|for
control|(
name|Field
name|field
range|:
name|type
operator|.
name|getDeclaredFields
argument_list|()
control|)
block|{
name|Argument
name|argument
init|=
name|field
operator|.
name|getAnnotation
argument_list|(
name|Argument
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|argument
operator|!=
literal|null
condition|)
block|{
name|arguments
operator|.
name|put
argument_list|(
name|argument
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|int
name|index
init|=
name|argument
operator|.
name|index
argument_list|()
decl_stmt|;
while|while
condition|(
name|orderedArguments
operator|.
name|size
argument_list|()
operator|<=
name|index
condition|)
block|{
name|orderedArguments
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|orderedArguments
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Duplicate argument index: "
operator|+
name|index
argument_list|)
throw|;
block|}
name|orderedArguments
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|argument
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Check indexes are correct
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|orderedArguments
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|orderedArguments
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Missing argument for index: "
operator|+
name|i
argument_list|)
throw|;
block|}
block|}
comment|// Populate
name|Map
argument_list|<
name|Argument
argument_list|,
name|Object
argument_list|>
name|argumentValues
init|=
operator|new
name|HashMap
argument_list|<
name|Argument
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|argIndex
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Object
argument_list|>
name|it
init|=
name|params
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Object
name|param
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|argIndex
operator|>=
name|orderedArguments
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Too many arguments specified"
argument_list|)
throw|;
block|}
name|Argument
name|argument
init|=
name|orderedArguments
operator|.
name|get
argument_list|(
name|argIndex
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|argument
operator|.
name|multiValued
argument_list|()
condition|)
block|{
name|argIndex
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|argument
operator|.
name|multiValued
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|l
init|=
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|argumentValues
operator|.
name|get
argument_list|(
name|argument
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|==
literal|null
condition|)
block|{
name|l
operator|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|argumentValues
operator|.
name|put
argument_list|(
name|argument
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
name|l
operator|.
name|add
argument_list|(
name|param
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|argumentValues
operator|.
name|put
argument_list|(
name|argument
argument_list|,
name|param
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Argument
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|argumentValues
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Field
name|field
init|=
name|arguments
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|value
init|=
name|convert
argument_list|(
name|action
argument_list|,
name|session
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|field
operator|.
name|getGenericType
argument_list|()
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|field
operator|.
name|set
argument_list|(
name|action
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Object
name|convert
parameter_list|(
name|Action
name|action
parameter_list|,
name|CommandSession
name|commandSession
parameter_list|,
name|Object
name|o
parameter_list|,
name|Type
name|type
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|blueprintConverter
operator|.
name|convert
argument_list|(
name|o
argument_list|,
operator|new
name|GenericType
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Action
name|createNewAction
parameter_list|()
block|{
name|Action
name|action
init|=
operator|(
name|Action
operator|)
name|blueprintContainer
operator|.
name|getComponentInstance
argument_list|(
name|actionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|action
operator|instanceof
name|BlueprintContainerAware
condition|)
block|{
operator|(
operator|(
name|BlueprintContainerAware
operator|)
name|action
operator|)
operator|.
name|setBlueprintContainer
argument_list|(
name|blueprintContainer
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|action
operator|instanceof
name|BundleContextAware
condition|)
block|{
name|BundleContext
name|context
init|=
operator|(
name|BundleContext
operator|)
name|blueprintContainer
operator|.
name|getComponentInstance
argument_list|(
literal|"blueprintBundleContext"
argument_list|)
decl_stmt|;
operator|(
operator|(
name|BundleContextAware
operator|)
name|action
operator|)
operator|.
name|setBundleContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
return|return
name|action
return|;
block|}
block|}
end_class

end_unit

