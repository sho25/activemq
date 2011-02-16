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
name|openwire
operator|.
name|tool
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
name|FileWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Project
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|taskdefs
operator|.
name|FixCRLF
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jam
operator|.
name|JClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jam
operator|.
name|JProperty
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jam
operator|.
name|JamClassIterator
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MultiSourceGenerator
extends|extends
name|OpenWireGenerator
block|{
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|manuallyMaintainedClasses
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|File
name|destDir
decl_stmt|;
specifier|protected
name|File
name|destFile
decl_stmt|;
specifier|protected
name|JClass
name|jclass
decl_stmt|;
specifier|protected
name|JClass
name|superclass
decl_stmt|;
specifier|protected
name|String
name|simpleName
decl_stmt|;
specifier|protected
name|String
name|className
decl_stmt|;
specifier|protected
name|String
name|baseClass
decl_stmt|;
specifier|protected
name|StringBuffer
name|buffer
decl_stmt|;
specifier|public
name|MultiSourceGenerator
parameter_list|()
block|{
name|initialiseManuallyMaintainedClasses
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Object
name|run
parameter_list|()
block|{
if|if
condition|(
name|destDir
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No destDir defined!"
argument_list|)
throw|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" generating files in: "
operator|+
name|destDir
argument_list|)
expr_stmt|;
name|destDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|buffer
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
name|JamClassIterator
name|iter
init|=
name|getClasses
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
try|try
block|{
name|jclass
operator|=
name|iter
operator|.
name|nextClass
argument_list|()
expr_stmt|;
if|if
condition|(
name|isValidClass
argument_list|(
name|jclass
argument_list|)
condition|)
block|{
name|processClass
argument_list|(
name|jclass
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Unable to process: "
operator|+
name|jclass
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Returns all the valid properties available on the current class      */
specifier|public
name|List
argument_list|<
name|JProperty
argument_list|>
name|getProperties
parameter_list|()
block|{
name|List
argument_list|<
name|JProperty
argument_list|>
name|answer
init|=
operator|new
name|ArrayList
argument_list|<
name|JProperty
argument_list|>
argument_list|()
decl_stmt|;
name|JProperty
index|[]
name|properties
init|=
name|jclass
operator|.
name|getDeclaredProperties
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|properties
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|JProperty
name|property
init|=
name|properties
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|isValidProperty
argument_list|(
name|property
argument_list|)
condition|)
block|{
name|answer
operator|.
name|add
argument_list|(
name|property
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|answer
return|;
block|}
specifier|protected
name|boolean
name|isValidClass
parameter_list|(
name|JClass
name|jclass
parameter_list|)
block|{
if|if
condition|(
name|jclass
operator|.
name|getAnnotation
argument_list|(
literal|"openwire:marshaller"
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|!
name|manuallyMaintainedClasses
operator|.
name|contains
argument_list|(
name|jclass
operator|.
name|getSimpleName
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|void
name|processClass
parameter_list|(
name|JClass
name|jclass
parameter_list|)
block|{
name|simpleName
operator|=
name|jclass
operator|.
name|getSimpleName
argument_list|()
expr_stmt|;
name|superclass
operator|=
name|jclass
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" processing class: "
operator|+
name|simpleName
argument_list|)
expr_stmt|;
name|className
operator|=
name|getClassName
argument_list|(
name|jclass
argument_list|)
expr_stmt|;
name|destFile
operator|=
operator|new
name|File
argument_list|(
name|destDir
argument_list|,
name|className
operator|+
name|filePostFix
argument_list|)
expr_stmt|;
name|baseClass
operator|=
name|getBaseClassName
argument_list|(
name|jclass
argument_list|)
expr_stmt|;
name|PrintWriter
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|out
operator|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|destFile
argument_list|)
argument_list|)
expr_stmt|;
name|generateFile
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Use the FixCRLF Ant Task to make sure the file has consistent
comment|// newlines
comment|// so that SVN does not complain on checkin.
name|Project
name|project
init|=
operator|new
name|Project
argument_list|()
decl_stmt|;
name|project
operator|.
name|init
argument_list|()
expr_stmt|;
name|FixCRLF
name|fixCRLF
init|=
operator|new
name|FixCRLF
argument_list|()
decl_stmt|;
name|fixCRLF
operator|.
name|setProject
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|fixCRLF
operator|.
name|setSrcdir
argument_list|(
name|destFile
operator|.
name|getParentFile
argument_list|()
argument_list|)
expr_stmt|;
name|fixCRLF
operator|.
name|setIncludes
argument_list|(
name|destFile
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fixCRLF
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|void
name|generateFile
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|protected
name|String
name|getBaseClassName
parameter_list|(
name|JClass
name|jclass
parameter_list|)
block|{
name|String
name|answer
init|=
literal|"BaseDataStructure"
decl_stmt|;
if|if
condition|(
name|superclass
operator|!=
literal|null
condition|)
block|{
name|String
name|name
init|=
name|superclass
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"Object"
argument_list|)
condition|)
block|{
name|answer
operator|=
name|name
expr_stmt|;
block|}
block|}
return|return
name|answer
return|;
block|}
specifier|protected
name|String
name|getClassName
parameter_list|(
name|JClass
name|jclass
parameter_list|)
block|{
return|return
name|jclass
operator|.
name|getSimpleName
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isAbstractClass
parameter_list|()
block|{
return|return
name|jclass
operator|!=
literal|null
operator|&&
name|jclass
operator|.
name|isAbstract
argument_list|()
return|;
block|}
specifier|public
name|String
name|getAbstractClassText
parameter_list|()
block|{
return|return
name|isAbstractClass
argument_list|()
condition|?
literal|"abstract "
else|:
literal|""
return|;
block|}
specifier|public
name|boolean
name|isMarshallerAware
parameter_list|()
block|{
return|return
name|isMarshallAware
argument_list|(
name|jclass
argument_list|)
return|;
block|}
specifier|protected
name|void
name|initialiseManuallyMaintainedClasses
parameter_list|()
block|{
name|String
index|[]
name|names
init|=
block|{
literal|"ActiveMQDestination"
block|,
literal|"ActiveMQTempDestination"
block|,
literal|"ActiveMQQueue"
block|,
literal|"ActiveMQTopic"
block|,
literal|"ActiveMQTempQueue"
block|,
literal|"ActiveMQTempTopic"
block|,
literal|"BaseCommand"
block|,
literal|"ActiveMQMessage"
block|,
literal|"ActiveMQTextMessage"
block|,
literal|"ActiveMQMapMessage"
block|,
literal|"ActiveMQBytesMessage"
block|,
literal|"ActiveMQStreamMessage"
block|,
literal|"ActiveMQBlobMessage"
block|,
literal|"DataStructureSupport"
block|,
literal|"WireFormatInfo"
block|,
literal|"ActiveMQObjectMessage"
block|}
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|manuallyMaintainedClasses
operator|.
name|add
argument_list|(
name|names
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getBaseClass
parameter_list|()
block|{
return|return
name|baseClass
return|;
block|}
specifier|public
name|void
name|setBaseClass
parameter_list|(
name|String
name|baseClass
parameter_list|)
block|{
name|this
operator|.
name|baseClass
operator|=
name|baseClass
expr_stmt|;
block|}
specifier|public
name|String
name|getClassName
parameter_list|()
block|{
return|return
name|className
return|;
block|}
specifier|public
name|void
name|setClassName
parameter_list|(
name|String
name|className
parameter_list|)
block|{
name|this
operator|.
name|className
operator|=
name|className
expr_stmt|;
block|}
specifier|public
name|File
name|getDestDir
parameter_list|()
block|{
return|return
name|destDir
return|;
block|}
specifier|public
name|void
name|setDestDir
parameter_list|(
name|File
name|destDir
parameter_list|)
block|{
name|this
operator|.
name|destDir
operator|=
name|destDir
expr_stmt|;
block|}
specifier|public
name|File
name|getDestFile
parameter_list|()
block|{
return|return
name|destFile
return|;
block|}
specifier|public
name|void
name|setDestFile
parameter_list|(
name|File
name|destFile
parameter_list|)
block|{
name|this
operator|.
name|destFile
operator|=
name|destFile
expr_stmt|;
block|}
specifier|public
name|JClass
name|getJclass
parameter_list|()
block|{
return|return
name|jclass
return|;
block|}
specifier|public
name|void
name|setJclass
parameter_list|(
name|JClass
name|jclass
parameter_list|)
block|{
name|this
operator|.
name|jclass
operator|=
name|jclass
expr_stmt|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getManuallyMaintainedClasses
parameter_list|()
block|{
return|return
name|manuallyMaintainedClasses
return|;
block|}
specifier|public
name|void
name|setManuallyMaintainedClasses
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|manuallyMaintainedClasses
parameter_list|)
block|{
name|this
operator|.
name|manuallyMaintainedClasses
operator|=
name|manuallyMaintainedClasses
expr_stmt|;
block|}
specifier|public
name|String
name|getSimpleName
parameter_list|()
block|{
return|return
name|simpleName
return|;
block|}
specifier|public
name|void
name|setSimpleName
parameter_list|(
name|String
name|simpleName
parameter_list|)
block|{
name|this
operator|.
name|simpleName
operator|=
name|simpleName
expr_stmt|;
block|}
specifier|public
name|JClass
name|getSuperclass
parameter_list|()
block|{
return|return
name|superclass
return|;
block|}
specifier|public
name|void
name|setSuperclass
parameter_list|(
name|JClass
name|superclass
parameter_list|)
block|{
name|this
operator|.
name|superclass
operator|=
name|superclass
expr_stmt|;
block|}
block|}
end_class

end_unit

