begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|memory
package|;
end_package

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
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  * Use any Map to implement the Cache.  No cache eviction going on here.  Just gives  * a Map a Cache interface.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|MapCache
implements|implements
name|Cache
block|{
specifier|protected
specifier|final
name|Map
name|map
decl_stmt|;
specifier|public
name|MapCache
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MapCache
parameter_list|(
name|Map
name|map
parameter_list|)
block|{
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
specifier|public
name|Object
name|put
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
return|return
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
specifier|public
name|Object
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
specifier|public
name|Object
name|remove
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|map
operator|.
name|remove
argument_list|(
name|key
argument_list|)
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|map
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

