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
name|kaha
operator|.
name|impl
operator|.
name|index
operator|.
name|hash
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
name|List
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
comment|/**  * Bin in a HashIndex  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
class|class
name|HashBin
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|HashBin
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|HashIndex
name|hashIndex
decl_stmt|;
specifier|private
name|int
name|id
decl_stmt|;
specifier|private
name|int
name|maximumEntries
decl_stmt|;
specifier|private
name|int
name|size
decl_stmt|;
specifier|private
name|List
argument_list|<
name|HashPageInfo
argument_list|>
name|hashPages
init|=
operator|new
name|ArrayList
argument_list|<
name|HashPageInfo
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Constructor      *       * @param hashIndex      * @param id      * @param maximumEntries      */
name|HashBin
parameter_list|(
name|HashIndex
name|hashIndex
parameter_list|,
name|int
name|id
parameter_list|,
name|int
name|maximumEntries
parameter_list|)
block|{
name|this
operator|.
name|hashIndex
operator|=
name|hashIndex
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|maximumEntries
operator|=
name|maximumEntries
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"HashBin["
operator|+
name|getId
argument_list|()
operator|+
literal|"]"
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|HashBin
condition|)
block|{
name|HashBin
name|other
init|=
operator|(
name|HashBin
operator|)
name|o
decl_stmt|;
name|result
operator|=
name|other
operator|.
name|id
operator|==
name|id
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|id
return|;
block|}
name|int
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
name|void
name|setId
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
name|int
name|getMaximumEntries
parameter_list|()
block|{
return|return
name|this
operator|.
name|maximumEntries
return|;
block|}
name|void
name|setMaximumEntries
parameter_list|(
name|int
name|maximumEntries
parameter_list|)
block|{
name|this
operator|.
name|maximumEntries
operator|=
name|maximumEntries
expr_stmt|;
block|}
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
name|HashPageInfo
name|addHashPageInfo
parameter_list|(
name|long
name|id
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|HashPageInfo
name|info
init|=
operator|new
name|HashPageInfo
argument_list|(
name|hashIndex
argument_list|)
decl_stmt|;
name|info
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|hashPages
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|+=
name|size
expr_stmt|;
return|return
name|info
return|;
block|}
specifier|public
name|HashEntry
name|find
parameter_list|(
name|HashEntry
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|HashEntry
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|int
name|low
init|=
literal|0
decl_stmt|;
name|int
name|high
init|=
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>
literal|1
decl_stmt|;
name|HashEntry
name|te
init|=
name|getHashEntry
argument_list|(
name|mid
argument_list|)
decl_stmt|;
name|int
name|cmp
init|=
name|te
operator|.
name|compareTo
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
name|result
operator|=
name|te
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|end
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
name|void
name|put
parameter_list|(
name|HashEntry
name|newEntry
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|boolean
name|replace
init|=
literal|false
decl_stmt|;
name|int
name|low
init|=
literal|0
decl_stmt|;
name|int
name|high
init|=
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>
literal|1
decl_stmt|;
name|HashEntry
name|midVal
init|=
name|getHashEntry
argument_list|(
name|mid
argument_list|)
decl_stmt|;
name|int
name|cmp
init|=
name|midVal
operator|.
name|compareTo
argument_list|(
name|newEntry
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|replace
operator|=
literal|true
expr_stmt|;
name|midVal
operator|.
name|setIndexOffset
argument_list|(
name|newEntry
operator|.
name|getIndexOffset
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|replace
condition|)
block|{
name|addHashEntry
argument_list|(
name|low
argument_list|,
name|newEntry
argument_list|)
expr_stmt|;
name|size
operator|++
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|end
argument_list|()
expr_stmt|;
block|}
block|}
name|HashEntry
name|remove
parameter_list|(
name|HashEntry
name|entry
parameter_list|)
throws|throws
name|IOException
block|{
name|HashEntry
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|int
name|low
init|=
literal|0
decl_stmt|;
name|int
name|high
init|=
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>
literal|1
decl_stmt|;
name|HashEntry
name|te
init|=
name|getHashEntry
argument_list|(
name|mid
argument_list|)
decl_stmt|;
name|int
name|cmp
init|=
name|te
operator|.
name|compareTo
argument_list|(
name|entry
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
name|result
operator|=
name|te
expr_stmt|;
name|removeHashEntry
argument_list|(
name|mid
argument_list|)
expr_stmt|;
name|size
operator|--
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|end
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|void
name|addHashEntry
parameter_list|(
name|int
name|index
parameter_list|,
name|HashEntry
name|entry
parameter_list|)
throws|throws
name|IOException
block|{
name|HashPageInfo
name|pageToUse
init|=
literal|null
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|index
operator|>=
name|maximumBinSize
argument_list|()
condition|)
block|{
name|HashPage
name|hp
init|=
name|hashIndex
operator|.
name|createPage
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|pageToUse
operator|=
name|addHashPageInfo
argument_list|(
name|hp
operator|.
name|getId
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|pageToUse
operator|.
name|setPage
argument_list|(
name|hp
argument_list|)
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|int
name|countSoFar
init|=
literal|0
decl_stmt|;
name|int
name|pageNo
init|=
literal|0
decl_stmt|;
for|for
control|(
name|HashPageInfo
name|page
range|:
name|hashPages
control|)
block|{
name|count
operator|+=
name|page
operator|.
name|size
argument_list|()
expr_stmt|;
if|if
condition|(
name|index
operator|<
name|count
condition|)
block|{
name|offset
operator|=
name|index
operator|-
name|countSoFar
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|index
operator|==
name|count
operator|&&
name|page
operator|.
name|size
argument_list|()
operator|+
literal|1
operator|<=
name|maximumEntries
condition|)
block|{
name|offset
operator|=
name|page
operator|.
name|size
argument_list|()
expr_stmt|;
break|break;
block|}
name|countSoFar
operator|+=
name|page
operator|.
name|size
argument_list|()
expr_stmt|;
name|pageNo
operator|++
expr_stmt|;
block|}
while|while
condition|(
name|pageNo
operator|>=
name|hashPages
operator|.
name|size
argument_list|()
condition|)
block|{
name|HashPage
name|hp
init|=
name|hashIndex
operator|.
name|createPage
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|addHashPageInfo
argument_list|(
name|hp
operator|.
name|getId
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|pageToUse
operator|=
name|hashPages
operator|.
name|get
argument_list|(
name|pageNo
argument_list|)
expr_stmt|;
block|}
name|pageToUse
operator|.
name|begin
argument_list|()
expr_stmt|;
name|pageToUse
operator|.
name|addHashEntry
argument_list|(
name|offset
argument_list|,
name|entry
argument_list|)
expr_stmt|;
name|doOverFlow
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
specifier|private
name|HashEntry
name|removeHashEntry
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|IOException
block|{
name|HashPageInfo
name|page
init|=
name|getRetrievePage
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
name|getRetrieveOffset
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|HashEntry
name|result
init|=
name|page
operator|.
name|removeHashEntry
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|doUnderFlow
argument_list|(
name|index
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|HashEntry
name|getHashEntry
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|IOException
block|{
name|HashPageInfo
name|page
init|=
name|getRetrievePage
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|page
operator|.
name|begin
argument_list|()
expr_stmt|;
name|int
name|offset
init|=
name|getRetrieveOffset
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|HashEntry
name|result
init|=
name|page
operator|.
name|getHashEntry
argument_list|(
name|offset
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|int
name|maximumBinSize
parameter_list|()
block|{
return|return
name|maximumEntries
operator|*
name|hashPages
operator|.
name|size
argument_list|()
return|;
block|}
specifier|private
name|HashPageInfo
name|getRetrievePage
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|IOException
block|{
name|HashPageInfo
name|result
init|=
literal|null
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|int
name|pageNo
init|=
literal|0
decl_stmt|;
for|for
control|(
name|HashPageInfo
name|page
range|:
name|hashPages
control|)
block|{
name|count
operator|+=
name|page
operator|.
name|size
argument_list|()
expr_stmt|;
if|if
condition|(
name|index
operator|<
name|count
condition|)
block|{
break|break;
block|}
name|pageNo
operator|++
expr_stmt|;
block|}
name|result
operator|=
name|hashPages
operator|.
name|get
argument_list|(
name|pageNo
argument_list|)
expr_stmt|;
name|result
operator|.
name|begin
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|int
name|getRetrieveOffset
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|result
init|=
literal|0
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|HashPageInfo
name|page
range|:
name|hashPages
control|)
block|{
if|if
condition|(
operator|(
name|index
operator|+
literal|1
operator|)
operator|<=
operator|(
name|count
operator|+
name|page
operator|.
name|size
argument_list|()
operator|)
condition|)
block|{
comment|// count=count==0?count:count+1;
name|result
operator|=
name|index
operator|-
name|count
expr_stmt|;
break|break;
block|}
name|count
operator|+=
name|page
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|void
name|doOverFlow
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|pageNo
init|=
name|index
operator|/
name|maximumEntries
decl_stmt|;
name|HashPageInfo
name|info
init|=
name|hashPages
operator|.
name|get
argument_list|(
name|pageNo
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|size
argument_list|()
operator|>
name|maximumEntries
condition|)
block|{
comment|// overflowed
name|info
operator|.
name|begin
argument_list|()
expr_stmt|;
name|HashEntry
name|entry
init|=
name|info
operator|.
name|removeHashEntry
argument_list|(
name|info
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|doOverFlow
argument_list|(
name|pageNo
operator|+
literal|1
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|doOverFlow
parameter_list|(
name|int
name|pageNo
parameter_list|,
name|HashEntry
name|entry
parameter_list|)
throws|throws
name|IOException
block|{
name|HashPageInfo
name|info
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|pageNo
operator|>=
name|hashPages
operator|.
name|size
argument_list|()
condition|)
block|{
name|HashPage
name|page
init|=
name|hashIndex
operator|.
name|createPage
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|info
operator|=
name|addHashPageInfo
argument_list|(
name|page
operator|.
name|getId
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|info
operator|.
name|setPage
argument_list|(
name|page
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|info
operator|=
name|hashPages
operator|.
name|get
argument_list|(
name|pageNo
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|begin
argument_list|()
expr_stmt|;
name|info
operator|.
name|addHashEntry
argument_list|(
literal|0
argument_list|,
name|entry
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|size
argument_list|()
operator|>
name|maximumEntries
condition|)
block|{
comment|// overflowed
name|HashEntry
name|overflowed
init|=
name|info
operator|.
name|removeHashEntry
argument_list|(
name|info
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|doOverFlow
argument_list|(
name|pageNo
operator|+
literal|1
argument_list|,
name|overflowed
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|doUnderFlow
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|int
name|index
parameter_list|)
block|{
comment|// does little
block|}
specifier|private
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|HashPageInfo
name|info
range|:
name|hashPages
control|)
block|{
name|info
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

