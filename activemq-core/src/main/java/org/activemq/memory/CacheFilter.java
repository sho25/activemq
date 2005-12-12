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

begin_comment
comment|/**  * Filters another Cache implementation.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|CacheFilter
implements|implements
name|Cache
block|{
specifier|protected
specifier|final
name|Cache
name|next
decl_stmt|;
specifier|public
name|CacheFilter
parameter_list|(
name|Cache
name|next
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
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
name|next
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
name|next
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
name|next
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
name|next
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|next
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

